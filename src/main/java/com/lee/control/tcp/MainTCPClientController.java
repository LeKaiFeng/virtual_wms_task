package com.lee.control.tcp;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import com.lee.database.mk.entity.*;
import com.lee.database.mk.service.impl.*;
import com.lee.netty.deal.DealRequestCommon;
import com.lee.netty.NettyClient;
import com.lee.netty.NettyClientHandler;
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

/**
 * @Date: 2022/12/6 9:05
 * @Description: TODO
 */
@FXMLController
@FXMLView(value = "/fxml/tcp/tcpClient.fxml")
@Component
@ConditionalOnProperty(name = "project.name", havingValue = "yfjq")
public class MainTCPClientController extends AbstractFxmlView implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(MainTCPClientController.class);
    @Value("${server.mfc.IP}")
    public String mfcIp;
    @Value("${server.mfc.port}")
    public String mfcPort;
    @Value("${server.mfc.end}")
    public String mfcEnd;
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
    protected BoxliftServiceImpl boxLiftService;
    @Autowired
    protected BoxliftshelfpdServiceImpl boxliftshelfpdService;
    @Autowired
    protected TaskServiceImpl taskService;
    @Autowired
    protected AreaServiceImpl areaService;
    @Autowired
    protected LocationsServiceImpl locationsService;
    @Autowired
    protected MessageReceiveServiceImpl messageReceiveService;
    public NettyClient nettyClient;
    public volatile int msgId = 1;
    protected int inboundBoxId;
    public volatile int outBoxId = 1;
    public volatile int moveBoxId = 1;
    List<Integer> levels = new ArrayList<>();
    List<Integer> aisles = new ArrayList<>();

    List<Boxlift> lifts = new ArrayList<>();

    protected final String site1 = "1079";
    protected final String site2 = "1081";

    //protected String deviceIp;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ThreadUtil.execute(this::initId);
        boxLiftService.allBoxLifts().forEach(boxLift -> {
            log.info("boxLift-{} 线程启动", boxLift.getId());
            lifts.add(boxLift);
            ThreadUtil.createThread("boxLift" + boxLift.getId(), () -> inboundTask(boxLift)).start();
        });
        connectBtn.setOnMouseClicked(event -> {
            log.info("开始连接mfc...");
            connect();
        });

        ip.setText(mfcIp);
        port.setText(mfcPort);
        log.info("init success");
    }

    public void initId() {
        //deviceIp = boxLiftService.getIp();
        msgId = messageReceiveService.initMegId();
        inboundBoxId = taskService.initTaskIdByType(1);
        outBoxId = taskService.initTaskIdByType(2);
        moveBoxId = taskService.initTaskIdByType(15);
        levels = locationsService.allLevels();
        aisles = locationsService.allAisles();
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
                        //.addLast(new DelimiterBasedFrameDecoder(10240, Unpooled.copiedBuffer(mfcEnd.getBytes())))
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
    public void putBoxOnLineByRequest() {
        if (autoInbound.isSelected()) {
            for (Boxlift lift : lifts) {
                int liftId = lift.getId();
                List<Task> totalTask = taskService.inboundTaskByLift(liftId);
                levels.forEach(level -> {
                    List<Task> currentLevelTask = totalTask.stream().filter(task -> task.getELevel().equals(level)).collect(Collectors.toList());
                    if (currentLevelTask.size() == 0) {
                        List<Locations> inbound = locationsService.inbound(level);
                        List<Task> taskList = new ArrayList<>();
                        aisles.forEach(aisle -> {
                            List<Locations> currentLevelAisle = inbound.stream().filter(in -> Objects.equals(in.getAisle(), aisle)).collect(Collectors.toList());
                            if (currentLevelAisle.size() > 0) {
                                String boxId = Constance.BOX_PREFIX + liftId + "-" + inboundBoxId;
                                Locations location = CommonUtil.randomFromList(currentLevelAisle);
                                location.setBoxNumber(boxId);
                                inbound.remove(location);
                                Task task = MKTaskTemplate.inboundTask(location);
                                if (liftId == 1) {
                                    task.setSite(site1);
                                } else if (liftId == 2) {
                                    task.setSite(site2);
                                }
                                taskList.add(task);
                                inboundBoxId++;
                                log.info("insert task level/location: {}/{} barcode:{}", level, location.getLocation(), boxId);
                            }
                        });
                        taskService.saveBatch(taskList);
                    }
                });
            }
        } else {
            ThreadUtil.sleep(1000);
        }

    }

    public void inboundTask(Boxlift boxlift) {
        int liftId = boxlift.getId();
        int liftPos = boxlift.getPos();
        int inboundId = taskService.initTaskIdByLift(liftPos);
        String deviceIp = boxlift.getIp();
        String site = (liftId == 1) ? site1 : site2;
        log.info("BoxLift-[{}], pos: {}, beginId: {}, ip: {} start...", liftId, liftPos, inboundId, deviceIp);
        while (true) {
            try {
                if (!autoInbound.isSelected()) {
                    ThreadUtil.sleep(1000);
                    continue;
                }
                //直接去task表找任务
                List<Task> tasks = taskService.inboundTaskGroupByLevel(liftId, site);
                List<Boxliftshelfpd> pds = boxliftshelfpdService.getPds(liftPos);
                for (Task task : tasks) {
                    int level = task.getELevel();
                    String boxId = task.getBoxNumber();
                    List<Boxliftshelfpd> pdList = pds.stream().filter(pd -> pd.getLevel() == level).collect(Collectors.toList());
                    if (pdList.size() == 1) {
                        Boxliftshelfpd pd = pdList.get(0);
                        if (pd.getInboundRequest() == 1 || pd.getInboundState() == 3) {
                            log.debug("boxLift-{} ,level: {}, pd-{}, exit box: {}", liftId, level, pd.getId(), pd.getInboundBox());
                            continue;
                        }
                        String requestLift = DeviceHttpRequest.sendRequest(deviceIp, 1, liftId, 1, boxId);
                        log.info("boxLift-[{}], request level:{}, boxNumber:{} {}", liftId, level, boxId, requestLift);
                        if (!requestLift.equalsIgnoreCase("success")) {
                            log.info("boxLift-[{}], level:{}, request {} failed,请检查设备IP", liftId, level, requestLift);
                            continue;
                        }
                        ThreadUtil.sleep(1000);

                        int times = 0;
                        do {
                            if (times > 0) {
                                log.info("boxLift-[{}], level:{} box:{} state≠1,wait task change 1 and times:{}", liftId, level, boxId, times);
                                ThreadUtil.sleep(1000);
                            }
                            times++;
                        } while (taskService.selectTask(1, 1, boxId).size() != 1);
                        String requestPD = DeviceHttpRequest.sendRequest(deviceIp, 2, pd.getId(), 1, boxId);
                        log.info("PD---[{}], request level:{}, boxNumber:{} {}", pd.getId(), level, boxId, requestPD);
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
            levels.forEach(level -> {
                List<Task> totalTasks = taskService.outboundTask(level);
                if (totalTasks.size() == 0) {
                    List<Locations> outLocations = locationsService.outboundLocations(level);
                    aisles.forEach(aisle -> {
                        if (!autoOutbound.isSelected()) {
                            return;
                        }
                        List<Locations> locations = outLocations.stream().
                                filter(lt -> Objects.equals(lt.getAisle(), aisle)).collect(Collectors.toList());
                        //List<Locations> locations = locationsService.outboundLocations(level, aisle);
                        if (locations.size() == 0) {
                            log.info("level:{} aisle:{} 无出库货位可用", level, aisle);
                            return;
                        }
                        List<Task> tasks = taskService.outboundTaskLF(level, aisle);
                        for (int i = tasks.size(); i < times; i++) {
                            Locations outLocation = CommonUtil.randomFromList(locations);
                            locations.remove(outLocation);
                            String boxId = Constance.OUT_BOX_PREFIX + outBoxId;
                            outLocation.setBoxNumber(boxId);
                            String request = DealRequestCommon.pushAppointStockOutJQYF(msgId, outLocation);
                            log.info("出库 - > level:{}, boxId:{}", level, boxId);
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
            levels.forEach(level -> {
                List<Task> totalMoveTasks = taskService.moveTask(level);
                // 每次 随机一个巷道生成一条跨层任务
                Integer aisleChoose = CommonUtil.randomFromList(aisles);
                if (totalMoveTasks.size() <= 2) {
                    aisles.forEach(aisle -> {
                        if (!autoMove.isSelected()) {
                            return;
                        }
                        List<Locations> startLocations = locationsService.outboundLocations(level, aisle);
                        for (int i = totalMoveTasks.size(); i < times; i++) {
                            Locations start = CommonUtil.randomFromList(startLocations);
                            String boxId = Constance.MOVE_BOX_PREFIX + moveBoxId;
                            start.setBoxNumber(boxId);
                            startLocations.remove(start);

                            List<Integer> moveAisles = aisles;
                            if (moveExitArea) {
                                int group = areaService.getGroup(level, start.getLocation() / 10000);
                                List<Area> groups = areaService.areas(level);
                                if (groups.size() > 1) {
                                    moveAisles = areaService.getAisles(level, group, moveExitArea);
                                }
                            }


                            //跨层
                            //Integer hLevel = level;
                            //if (aisleChoose == aisle && i == (times - 1)) {
                            //    hLevel = CommonUtil.randomFromList(levels);
                            //}

                            Locations end = locationsService.moveLocation(level, moveAisles);
                            if (end == null) {
                                log.info("move task level:{} , aisle:{}, no end ", level, aisles.toString());
                                continue;
                            }
                            log.info("移库 - > s_level/s_location: {}/{}, e_level/e_location: {}/{} boxId:{}",
                                    start.getLevel(), start.getLocation(), end.getLevel(), end.getLocation(), boxId);
                            String request = DealRequestCommon.pushMoveLibraryJQYF(msgId, start, end);
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
            boolean write = nettyClient.write(Unpooled.wrappedBuffer((request + "\r\n").getBytes()));
            log.info("client send : {}", request);
            return write;
        } else {
            log.info("尚未建立连接，发送失败 msg:{} ...", request);
        }
        return false;
    }
}