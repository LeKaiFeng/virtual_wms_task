package com.lee.control.tcp;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import com.lee.database.dss.entity.*;
import com.lee.database.dss.service.impl.*;
import com.lee.netty.NettyClient;
import com.lee.netty.NettyClientHandler;
import com.lee.netty.deal.DealDSSRequest;
import com.lee.util.*;
import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.FXMLView;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@FXMLController
@FXMLView(value = "/fxml/tcp/tcpClientDsl.fxml")
@Component
@ConditionalOnProperty(name = "project.name", havingValue = "dsl")
public class MainTCPClientDslController extends AbstractFxmlView implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(MainTCPClientDslController.class);
    @Value("${server.GSEE.IP}")
    public String gseeIp;
    @Value("${server.GSEE.port}")
    public String gseePort;
    @Value("${server.WMS.end}")
    public String wmsEnd;
    @Value("${server.GSEE.end}")
    public String gseeEnd;
    @Value("${strategy.inbound.machine}")
    public String inboundMachine;
    @Value("${strategy.inbound.isAppoint}")
    public boolean isAppoint;
    @Value("${strategy.inbound.intervalTime}")
    public Long inboundIntervalTime;
    @Value("${strategy.inbound.exitArea}")
    public boolean inboundExitArea;
    @Value("${strategy.outbound.everyLevelNums}")
    public int outboundEveryLevelNums;
    @Value("${strategy.outbound.ignoreExitTask}")
    public boolean outboundIgnoreExitTask;
    //@Value("${strategy.outbound.exitArea}")
    //public boolean outboundExitArea;
    //@Value("${strategy.move.intervalTime}")
    //public Long moveIntervalTime;
    @Value("${strategy.move.everyLevelNums}")
    public int moveEveryLevelNums;
    @Value("${strategy.move.ignoreExitTask}")
    public boolean moveIgnoreExitTask;
    @Value("${strategy.move.exitArea}")
    public boolean moveExitArea;

    @FXML
    protected JFXToggleButton autoInbound, autoOutbound, autoMove;
    @FXML
    protected TextField ip, port;
    @FXML
    protected JFXButton connectBtn, description;
    @Autowired
    protected DeviceBoxLiftServiceImpl boxLiftService;
    @Autowired
    protected DeviceShelfPdServiceImpl boxliftshelfpdService;
    @Autowired
    protected ResourceTaskServiceImpl taskService;
    @Autowired
    protected RunningAreaServiceImpl areaService;
    @Autowired
    protected ResourceLocationServiceImpl locationsService;
    @Autowired
    protected GaRunningMessageReceiveServiceImpl messageReceiveService;
    public NettyClient nettyClient;
    public volatile int msgId = 1;
    protected int inboundBoxId;
    public volatile int outBoxId = 1;
    public volatile int moveBoxId = 1;
    List<Integer> levels = new ArrayList<>();
    List<Integer> aisles = new ArrayList<>();
    List<DeviceBoxLift> lifts = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ThreadUtil.execute(this::initId);
        boxLiftService.list().forEach(boxLift -> {
            log.info("boxLift-{} 线程启动", boxLift.getId());
            lifts.add(boxLift);
//            ThreadUtil.createThread("boxLift" + boxLift.getId(), () -> sendRequest(boxLift)).start();
        });
        connectBtn.setOnMouseClicked(event -> {
            log.info("开始连接GSEE...");
            connect();
        });

        ip.setText(gseeIp);
        port.setText(gseePort);
        log.info("init success");
    }

    public void initId() {
        msgId = messageReceiveService.count() + 1;
        inboundBoxId = taskService.initTaskIdByType(1);
        outBoxId = taskService.initTaskIdByType(2);
        moveBoxId = taskService.initTaskIdByType(15);
        levels = locationsService.levels();
        aisles = locationsService.aisles();
        log.info("init msgId: {}, outBoxId: {}, moveBoxId: {}", msgId, outBoxId, moveBoxId);
        log.info("levels: {}", ArrayUtil.toString(levels));
        log.info("aisles: {}", ArrayUtil.toString(aisles));
    }

    public void connect() {
        String ipValue = StrUtil.toString(ip.getText());
        Integer portValue = Convert.toInt(port.getText());
        nettyClient = new NettyClient(ipValue, portValue, new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline()
                        //.addLast(new DelimiterBasedFrameDecoder(10240, Unpooled.copiedBuffer("\r\n".getBytes())))
                        .addLast(new DelimiterBasedFrameDecoder(10240, Unpooled.copiedBuffer(gseeEnd.getBytes())))
                        .addLast(new StringDecoder())
                        .addLast(new StringEncoder())
                        .addLast("clientHandler", new NettyClientHandler());
            }
        }, true, 10 * 1000);
        log.info("初始化netty, {}:{}", ipValue, portValue);
        ThreadUtil.createThread("wmsClient - init", nettyClient::init).start();
        connectBtn.setDisable(true);
        ip.setDisable(true);
        port.setDisable(true);
    }

    @Scheduled(fixedRate = 1000, initialDelay = 1000 * 3)
    public void pushTaskGenerate() {
        if (autoInbound.isSelected()) {
            for (DeviceBoxLift lift : lifts) {
                int liftId = lift.getId();
                List<ResourceTask> totalTask = taskService.getLiftInboundTask(-1, liftId);
                levels.forEach(level -> {
                    List<ResourceTask> currentLevelTask = totalTask.stream().filter(task ->
                            task.getStartLevel().equals(level)).collect(Collectors.toList());
                    if (currentLevelTask.size() == 0) {
                        List<ResourceLocation> inbound = locationsService.inboundLocations(level, aisles);
                        aisles.forEach(aisle -> {
                            if (autoInbound.isSelected()) {


                                List<ResourceLocation> currentLevelAisle = inbound.stream().filter(in -> Objects.equals(in.getAisle(), aisle)).collect(Collectors.toList());
                                if (currentLevelAisle.size() > 0) {
                                    String boxId = Constance.BOX_PREFIX + liftId + "-" + inboundBoxId;
                                    ResourceLocation location = CommonUtil.randomFromList(currentLevelAisle);
                                    if (location == null) {
                                        log.info("货位确失-> level:{}, aisle:{}", level, aisle);
                                        return;
                                    }
                                    location.setBarcode(boxId);
                                    inbound.remove(location);
                                    String request = DealDSSRequest.pushTaskGenerate(1, msgId, liftId, boxId, null, location);
                                    boolean tcpSend = send(request);
                                    int times = 0;
                                    if (!tcpSend) {
                                        do {
                                            times++;
                                            log.info("入库 发送失败并重发");
                                            ThreadUtil.sleep(500);
                                        } while (send(request) && times < 10);
                                    }
                                    if (times == 10) {
                                        log.info("{}次重发未成功,request:{}", times, request);
                                    }
                                    ThreadUtil.sleep(500);
                                    inboundBoxId++;
                                    msgId++;
                                    log.info("insert task  barcode:{} to level/location: {}/{} ", boxId, level, location.getLocation());
                                }
                            }
                        });
                    }
                });
            }
        } else {
            ThreadUtil.sleep(1000);
        }

    }

    public void sendRequest(DeviceBoxLift boxlift) {
        int liftId = boxlift.getId();
        int liftPos = boxlift.getPos();
//        int inboundId = taskService.initTaskIdByLift(liftPos);
        String deviceIp = boxlift.getIp();
        log.info("BoxLift-[{}], pos: {}, ip: {}, start...", liftId, liftPos, deviceIp);
        while (true) {
            try {
                if (!autoInbound.isSelected()) {
                    ThreadUtil.sleep(1000);
                    continue;
                }
                List<ResourceTask> tasks = taskService.getLiftInboundTask(-1, liftId);
                //直接去task表找任务
                List<DeviceShelfPd> pds = boxliftshelfpdService.selectPds(liftPos);
                for (ResourceTask task : tasks) {
                    int level = task.getStartLevel();
                    String boxId = task.getBarcode();
                    List<DeviceShelfPd> pdList = pds.stream().filter(pd -> pd.getLevel() == level).collect(Collectors.toList());
                    if (pdList.size() == 1) {
                        DeviceShelfPd pd = pdList.get(0);
                        if (pd.getInboundRequest() == 1 || pd.getInboundState() == 3) {
                            log.info("boxLift-{} ,level: {}, pd-{}, exit box: {}", liftId, level, pd.getId(), pd.getInboundBarcode());
                            continue;
                        }
                        String requestLift = DeviceHttpRequest.sendRequest(deviceIp, 1, liftId, 1, boxId);
                        log.info("boxLift-[{}], request level:{}, barcode:{} {}", liftId, level, boxId, requestLift);
                        if (!requestLift.equalsIgnoreCase("success")) {
                            log.info("boxLift-[{}], level:{}, request {} failed,请检查设备IP", liftId, level, requestLift);
                            continue;
                        }
                        ThreadUtil.sleep(1000);

                        int times = 0;
                        do {
                            if (times > 0) {
                                log.info("boxLift-[{}], level:{} box:{} state≠1,wait task change 1 and times:{}", liftId, level, boxId, times);
                                ThreadUtil.sleep(500);
                            }
                            times++;
                        } while (taskService.isOnLiftTask(boxId, level).getState() != 1 && times < 10);
                        if (times == 10) {
                            log.info("10次请求失败,不发了");
                            continue;
                        }
                        String requestPD = DeviceHttpRequest.sendRequest(deviceIp, 2, pd.getId(), 1, boxId);
                        log.info("PD---[{}], request level:{}, barcode:{} {}", pd.getId(), level, boxId, requestPD);
                        ThreadUtil.sleep(500);
                    }
                }
                ThreadUtil.sleep(1000);
            } catch (Exception e) {
                log.info(e.getMessage());
            }
        }
    }

    public int getTimes(int createNum) {
        int aisleNum = aisles.size();
        int num = 1;
        if (createNum < aisleNum) {
            return num;
        } else {
            if (createNum % aisleNum == 0) {
                return createNum / aisleNum;
            } else {
                return createNum / aisleNum + 1;
            }
        }
    }

    @Scheduled(fixedRateString = "${strategy.outbound.intervalTime}", initialDelay = 1000 * 3)
    // 延时10s启动，之后每5s执行一次
    public void outboundTask() {
        try {
            if (!autoOutbound.isSelected()) {
                return;
            }
            int times = getTimes(outboundEveryLevelNums);
            List<ResourceTask> totalTasks = taskService.searchOutboundTask(-1, null);
            levels.forEach(level -> {
                List<ResourceTask> currentLevelTask = totalTasks.stream().filter(task -> task.getStartLevel().equals(level)).collect(Collectors.toList());
                if (currentLevelTask.size() == 0) {
                    List<ResourceLocation> outLocations = locationsService.outLocations(level, aisles);
                    aisles.forEach(aisle -> {
                        if (!autoOutbound.isSelected()) {
                            return;
                        }
                        List<ResourceLocation> locations = outLocations.stream().filter(lt -> Objects.equals(lt.getAisle(), aisle)).collect(Collectors.toList());
                        if (locations.size() == 0) {
                            log.info("level:{} aisle:{} 无出库货位可用", level, aisle);
                            return;
                        }
                        List<ResourceTask> currentLevelAndAisleTask = totalTasks.stream().filter(task ->
                                task.getStartLevel().equals(level) && task.getStartAisle().equals(aisle)).collect(Collectors.toList());

                        for (int i = currentLevelAndAisleTask.size(); i < times; i++) {
                            ResourceLocation outLocation = CommonUtil.randomFromList(locations);
                            locations.remove(outLocation);
                            String boxId = Constance.OUT_BOX_PREFIX + outBoxId;
                            outLocation.setBarcode(boxId);
                            String request = DealDSSRequest.pushTaskGenerate(2, msgId, 0, boxId, outLocation, null);
                            log.info("出库 - > level/location:{}/{}, boxId:{}", level, outLocation.getLocation(), boxId);
                            boolean tcpSend = send(request);
                            if (!tcpSend) {
                                do {
                                    log.info("发送失败,重发");
                                    ThreadUtil.sleep(500);
                                } while (send(request));
                            }
                            msgId++;
                            outBoxId++;
                        }
                        ThreadUtil.sleep(500);
                    });
                }
            });
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }


    @Scheduled(initialDelay = 1000 * 5, fixedRateString = "${strategy.move.intervalTime}") // 延时10s启动，之后每5s执行一次
    public void moveTask() {
        try {
            if (!autoMove.isSelected()) {
                return;
            }
            int times = getTimes(moveEveryLevelNums);
            List<ResourceTask> totalMoveTask = taskService.searchMoveTask(-1, null);
            levels.forEach(level -> {
                List<ResourceTask> currentLevelMoveTask = totalMoveTask.stream().filter(task -> task.getStartLevel().equals(level)).collect(Collectors.toList());
                // 每次 随机一个巷道生成一条跨层任务
                Integer aisleChoose = CommonUtil.randomFromList(aisles);
                if (currentLevelMoveTask.size() <= 1) {
                    aisles.forEach(aisle -> {
                        if (!autoMove.isSelected()) {
                            return;
                        }
                        List<ResourceLocation> startLocations = locationsService.outLocations(level, aisles);
                        for (int i = currentLevelMoveTask.size(); i < times; i++) {
                            ResourceLocation start = CommonUtil.randomFromList(startLocations);
                            String boxId = Constance.MOVE_BOX_PREFIX + moveBoxId;
                            start.setBarcode(boxId);
                            startLocations.remove(start);
                            List<Integer> moveAisles = aisles;

                            List<ResourceLocation> endLocations = locationsService.inboundLocations(level, aisles);
                            ResourceLocation end = CommonUtil.randomFromList(endLocations);
                            if (end == null) {
                                log.info("move task level:{} , aisle:{}, no end ", level, aisles.toString());
                                continue;
                            }
                            log.info("移库 - > s_level/s_location: {}/{}, e_level/e_location: {}/{} boxId:{}",
                                    start.getLevel(), start.getLocation(), end.getLevel(), end.getLocation(), boxId);
                            String request = DealDSSRequest.pushTaskGenerate(15, msgId, 0, boxId, start, end);
                            boolean tcpSend = send(request);
                            if (!tcpSend) {
                                do {
                                    log.info("移库 发送失败并重发");
                                    ThreadUtil.sleep(500);
                                } while (send(request));
                            }
                            msgId++;
                            moveBoxId++;
                            ThreadUtil.sleep(500);
                        }
                    });
                }
            });
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    public boolean send(String request) {
        if (nettyClient.isConnect()) {
            boolean write = nettyClient.write(Unpooled.wrappedBuffer((request + wmsEnd).getBytes()));
            log.info("client send : {}", request);
            return write;
        } else {
            log.info("尚未建立连接，发送失败 msg:{} ...", request);
        }
        return false;
    }
}