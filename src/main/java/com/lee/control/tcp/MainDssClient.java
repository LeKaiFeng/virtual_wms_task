package com.lee.control.tcp;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import com.lee.database.dss.entity.DeviceBoxLift;
import com.lee.database.dss.entity.DeviceShelfPd;
import com.lee.database.dss.entity.ResourceLocation;
import com.lee.database.dss.entity.ResourceTask;
import com.lee.database.dss.service.impl.*;
import com.lee.netty.NettyClient;
import com.lee.netty.NettyClientHandler;
import com.lee.netty.deal.DealDSSRequest;
import com.lee.util.CommonUtil;
import com.lee.util.Constance;
import com.lee.util.DeviceHttpRequest;
import com.lee.util.ThreadUtil;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@FXMLController
@FXMLView(value = "/fxml/tcp/dss/dssClient.fxml")
//@Component
public class MainDssClient extends AbstractFxmlView implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(MainDssClient.class);
    @Value("${server.device.ip}")
    public String deviceIp;
    @Value("${server.mfc.IP}")
    public String mfcIp;
    @Value("${server.mfc.port}")
    public String mfcPort;
    @Value("${server.WMS.end}")
    public String wmsEnd;
    @Value("${server.mfc.end}")
    public String mfcEnd;
    @Value("${strategy.inbound.machine}")
    public String inboundMachine;
    @Value("${strategy.inbound.isAppoint}")
    public boolean isAppoint;
    @Value("${strategy.inbound.intervalTime}")
    public Long inboundIntervalTime;
    @Value("${strategy.inbound.everyLevelNums}")
    public int inboundEveryLevelNums;
    @Value("${strategy.outbound.everyLevelNums}")
    public int outboundEveryLevelNums;
    @Value("${strategy.outbound.ignoreExitTask}")
    public boolean outboundIgnoreExitTask;
    @Value("${strategy.move.everyLevelNums}")
    public int moveEveryLevelNums;
    @Value("${strategy.move.ignoreExitTask}")
    public boolean moveIgnoreExitTask;

    @FXML
    protected JFXToggleButton autoInbound, autoReq, autoOutbound, autoMove;
    @FXML
    protected TextField ip, port;
    @FXML
    protected JFXButton connectBtn, description;
    @Resource
    protected DeviceBoxLiftServiceImpl boxLiftService;
    @Autowired
    protected DeviceShelfPdServiceImpl shelfPdService;
    @Autowired
    protected ResourceTaskServiceImpl taskService;
    @Autowired
    protected RunningAreaServiceImpl areaService;
    @Autowired
    protected ResourceLocationServiceImpl locationsService;
    @Autowired
    protected GaRunningMessageReceiveServiceImpl messageReceiveService;
    public NettyClient nettyClient;
    protected AtomicInteger msgNum = new AtomicInteger(1);
    public AtomicInteger outBoxNum = new AtomicInteger(1);
    public AtomicInteger moveBoxNum = new AtomicInteger(1);
    List<Integer> levels = new ArrayList<>();
    List<Integer> aisles = new ArrayList<>();
    List<DeviceBoxLift> lifts = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("This is dss tcp !");
        ThreadUtil.execute(this::initId);
        boxLiftService.allLift().forEach(boxLift -> {
            lifts.add(boxLift);
            ThreadUtil.createThread("TaskGenerate-" + boxLift.getId(), () -> pushTaskGenerate(boxLift)).start();
            ThreadUtil.createThread("MK-" + boxLift.getId(), () -> sendRequest(boxLift)).start();
        });
        connectBtn.setOnMouseClicked(event -> {
            log.info("开始连接mfc...");
            connect();
        });
        ip.setText(mfcIp);
        port.setText(mfcPort);
    }

    public void initId() {
        msgNum.set(messageReceiveService.count() + 1);
        outBoxNum.set(taskService.initTaskIdByType(2));
        moveBoxNum.set(taskService.initTaskIdByType(15));
        levels = locationsService.levels();
        aisles = locationsService.aisles();
        log.info("init msgId: {}, outBoxId: {}, moveBoxId: {}", msgNum.get(), outBoxNum.get(), moveBoxNum.get());
        log.info("levels: {}", ArrayUtil.toString(levels));
        log.info("aisles: {}", ArrayUtil.toString(aisles));
    }

    public void connect() {
        String ipValue = StrUtil.toString(ip.getText());
        Integer portValue = Convert.toInt(port.getText());
        nettyClient = new NettyClient(ipValue, portValue, new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) {
                socketChannel.pipeline()
                        .addLast(new DelimiterBasedFrameDecoder(10240, Unpooled.copiedBuffer(mfcEnd.getBytes())))
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

    public void pushTaskGenerate(DeviceBoxLift lift) {
        int liftId = lift.getId();
        int liftPos = lift.getPos();
        int num = taskService.initBoxId(liftPos);
        AtomicInteger boxNum = new AtomicInteger(num);
        log.info("mk-[{}], inbound boxId:{}", liftId, num);
        List<DeviceShelfPd> pds = shelfPdService.selectPds(liftPos);
        while (true) {
            try {
                if (autoInbound.isSelected()) {
                    List<ResourceTask> totalTask = taskService.getLiftInboundTask(-1, liftId);
                    levels.forEach(level -> {
                        List<DeviceShelfPd> validPds = pds.stream().filter(pd -> pd.getLevel().equals(level)).collect(Collectors.toList());
                        if (validPds.size() > 0) {
                            List<ResourceTask> currentLevelTask = totalTask.stream().filter(task ->
                                    task.getStartLevel().equals(level)).collect(Collectors.toList());
                            if (currentLevelTask.size() == 0) {
                                List<ResourceLocation> inbound = locationsService.inboundLocations(level, aisles);
                                for (int i = 0; i < inboundEveryLevelNums; i++) {
                                    if (!autoInbound.isSelected()) {
                                        continue;
                                    }
                                    Integer aisle = CommonUtil.randomFromList(aisles);
                                    String boxId = Constance.BOX_PREFIX + liftId + "-" + boxNum.getAndIncrement();
                                    List<ResourceLocation> currentLevelAisle = inbound.stream().filter(in -> Objects.equals(in.getAisle(), aisle)).collect(Collectors.toList());
                                    if (currentLevelAisle.size() == 0) {
                                        continue;
                                    }
                                    ResourceLocation location = CommonUtil.randomFromList(currentLevelAisle);
                                    if (location == null) {
                                        log.info("货位确失-> level:{}, aisle:{}", level, aisle);
                                        return;
                                    }
                                    location.setBarcode(boxId);
                                    inbound.remove(location);
                                    String request = DealDSSRequest.pushTaskGenerate(1, msgNum.getAndIncrement(), liftId, boxId, null, location);
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
                                    ThreadUtil.sleep(1000);
                                    log.info("push task barcode: {} to level/location: {}/{} ", boxId, level, location.getLocation());
                                }
                            }
                        }

                    });
                } else {
                    ThreadUtil.sleep(1000);
                }
            } catch (Exception e) {
                log.info("Lift-[{}], pushTaskGenerate failed close Thread; {}", liftId, e.getMessage());
                break;
            }
        }
    }

    public void sendRequest(DeviceBoxLift boxLift) {
        int liftId = boxLift.getId();
        int liftPos = boxLift.getPos();
        log.info("BoxLift-[{}], pos: {}, ip: {}, start...", liftId, liftPos, deviceIp);
        while (true) {
            try {
                if (!autoReq.isSelected()) {
                    ThreadUtil.sleep(1000);
                    continue;
                }
                List<ResourceTask> tasks = taskService.getLiftInboundTask(-1, liftId);
                for (ResourceTask task : tasks) {
                    if (!autoReq.isSelected()) {
                        ThreadUtil.sleep(1000);
                        continue;
                    }
                    int level = task.getStartLevel();
                    String boxId = task.getBarcode();

                    /*
                     * TODO 该提升机，该层，是否允许继续送箱子
                     *  1. pd表 对应leve、pos 没有箱子
                     *  2. task表 对应leve,pos,任务状态为0或1
                     */
                    DeviceShelfPd pd = shelfPdService.selectPd(level, liftPos);
                    if (pd == null) {
                        log.debug("Lift-[{}], {}层, 没有入库PD", liftId, level);
                        continue;
                    }
                    if (pd.getInboundRequest() == 1 || pd.getInboundState() == 3) {
                        log.debug("boxLift-{} ,level: {}, pd-{}, box: {} 存在入库请求", liftId, level, pd.getId(), pd.getInboundBarcode());
                        continue;
                    }
                    if (task.getState() == 0) {
                        String requestLift = DeviceHttpRequest.sendDslLiftRequest(deviceIp, 1, liftId, 1, boxId, -1, -1, -1);
                        log.info("boxLift-[{}], request level:{}, barcode:{} {}", liftId, level, boxId, requestLift);
                        if (!requestLift.equalsIgnoreCase("success")) {
                            log.info("boxLift-[{}], level:{}, request {} failed,请检查设备IP", liftId, level, requestLift);
                            continue;
                        }
                        ThreadUtil.sleep(1000);
                        int times = 0;
                        do {
                            if (times > 0) {
                                ThreadUtil.sleep(1000);
                            }
                            times++;
                        } while (taskService.isOnLiftTask(boxId, level).getState() != 1 && times < 10);
                        if (times == 10) {
                            log.info("boxLift-[{}], level:{}, box:{} state≠1 ,等待超时,不发了", liftId, level, boxId);
                            continue;
                        }
                    }
                    if (task.getState() == 7) {
                        continue;
                    }
                    String requestPD = DeviceHttpRequest.senDslPDRequest(deviceIp, 2, pd.getId(), 1, boxId, -1, -1, -1);
                    log.info("PD---[{}], request level:{}, barcode:{} {}", pd.getId(), level, boxId, requestPD);
                    ThreadUtil.sleep(1000);
                }
                ThreadUtil.sleep(5000);
            } catch (Exception e) {
                log.info("Lift-{} request failed close Thread, {}", liftId, e.getMessage());
                break;
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
                            String boxId = Constance.OUT_BOX_PREFIX + outBoxNum.getAndIncrement();
                            outLocation.setBarcode(boxId);
                            String request = DealDSSRequest.pushTaskGenerate(2, msgNum.getAndIncrement(), 0, boxId, outLocation, null);
                            log.info("出库 - > level/location:{}/{}, boxId:{}", level, outLocation.getLocation(), boxId);
                            boolean tcpSend = send(request);
                            if (!tcpSend) {
                                do {
                                    log.info("发送失败,重发");
                                    ThreadUtil.sleep(500);
                                } while (send(request));
                            }
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
//                Integer aisleChoose = CommonUtil.randomFromList(aisles);
                if (currentLevelMoveTask.size() <= 1) {
                    aisles.forEach(aisle -> {
                        if (!autoMove.isSelected()) {
                            return;
                        }
                        List<ResourceLocation> startLocations = locationsService.outLocations(level, aisles);
                        for (int i = currentLevelMoveTask.size(); i < times; i++) {
                            ResourceLocation start = CommonUtil.randomFromList(startLocations);
                            String boxId = Constance.MOVE_BOX_PREFIX + moveBoxNum.getAndIncrement();
                            start.setBarcode(boxId);
                            startLocations.remove(start);
//                            List<Integer> moveAisles = aisles;

                            List<ResourceLocation> endLocations = locationsService.inboundLocations(level, aisles);
                            ResourceLocation end = CommonUtil.randomFromList(endLocations);
                            if (end == null) {
                                log.info("move task level:{} , aisle:{}, no end ", level, aisles.toString());
                                continue;
                            }
                            log.info("移库 - > s_level/s_location: {}/{}, e_level/e_location: {}/{} boxId:{}",
                                    start.getLevel(), start.getLocation(), end.getLevel(), end.getLocation(), boxId);
                            String request = DealDSSRequest.pushTaskGenerate(15, msgNum.getAndIncrement(), 0, boxId, start, end);
                            boolean tcpSend = send(request);
                            if (!tcpSend) {
                                do {
                                    log.info("移库 发送失败并重发");
                                    ThreadUtil.sleep(500);
                                } while (send(request));
                            }
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