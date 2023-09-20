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
import com.lee.database.std.entity.*;
import com.lee.database.std.service.impl.*;
import com.lee.netty.NettyClient;
import com.lee.netty.NettyClientHandler;
import com.lee.netty.deal.DealDSSRequest;
import com.lee.netty.deal.DealSTDRequest;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@FXMLView(value = "/fxml/tcp/tcpClientCC.fxml")
@Component
@ConditionalOnProperty(name = "project.name", havingValue = "cc")
public class MainTCPClientCCController extends AbstractFxmlView implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(MainTCPClientCCController.class);
    @Value("${device.ip}")
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
    @Value("${strategy.outbound.everyLevelNums}")
    public int outboundEveryLevelNums;
    @Value("${strategy.outbound.ignoreExitTask}")
    public boolean outboundIgnoreExitTask;
    @Value("${strategy.move.everyLevelNums}")
    public int moveEveryLevelNums;
    @Value("${strategy.move.ignoreExitTask}")
    public boolean moveIgnoreExitTask;

    @FXML
    protected JFXToggleButton autoInbound, autoOutbound, autoMove;
    @FXML
    protected TextField ip, port;
    @FXML
    protected JFXButton connectBtn, description;

    public NettyClient nettyClient;
    protected AtomicInteger msgNum = new AtomicInteger(1);
    public AtomicInteger outBoxNum = new AtomicInteger(1);
    public AtomicInteger moveBoxNum = new AtomicInteger(1);
    List<Integer> levels = new ArrayList<>();
    List<Integer> aisles = new ArrayList<>();
    List<Boxlift> lifts = new ArrayList<>();

    @Autowired
    protected StdBoxliftServiceImpl boxLiftService;
    @Autowired
    protected StdTaskServiceImpl taskService;
    @Autowired
    protected StdLocationsServiceImpl locationsService;
    @Autowired
    protected StdBoxliftshelfpdServiceImpl shelfPdService;
    @Autowired
    protected StdAnnounceServiceImpl announceService;
    @Autowired
    protected StdAppointannounceServiceImpl appointAnnounceService;

    protected List<Locations> locations = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ThreadUtil.execute(this::initId);
        boxLiftService.query().select("id", "pos", "ip", "is_master").eq("is_master", 1).list().forEach(boxLift -> {
            if (boxLift.getId() != 1) {
                return;
            }
            log.info("boxLift-{} thread start", boxLift.getId());
            lifts.add(boxLift);
            ThreadUtil.createThread("TaskGenerate" + boxLift.getId(), () -> pushTaskMFC(boxLift)).start();
            ThreadUtil.createThread("MK-" + boxLift.getId(), () -> sendRequest(boxLift)).start();
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
        outBoxNum.set(taskService.initId(2));
        moveBoxNum.set(taskService.initId(15));
        levels = locationsService.allLevels();
        aisles = locationsService.allAisles();
        locations = locationsService.outLocations();
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

    public void pushTaskMFC(Boxlift lift) {
        int liftId = lift.getId();
        int liftPos = lift.getPos();
        AtomicInteger boxNum = new AtomicInteger(1);
        boxNum.set(taskService.initBoxId(liftPos));
        while (true) {
            try {
                if (autoInbound.isSelected()) {
                    List<Appointannounce> totalAppoint = appointAnnounceService.isExitAppoint(-1, liftId);
                    if (totalAppoint.size() == 0) {
                        List<Locations> locations = locationsService.inboundLocations(-1);
                        levels.forEach(level -> {
                            if (autoInbound.isSelected()) {
                                List<Appointannounce> currentAppoint = totalAppoint.stream().filter(appoint -> appoint.getLevel().equals(level)).collect(Collectors.toList());
                                List<Locations> currentLevelLocations = locations.stream().filter(lt -> lt.getLevel() == level).collect(Collectors.toList());
                                Locations inbound = CommonUtil.randomFromList(currentLevelLocations);
                                String boxId = Constance.BOX_PREFIX + liftId + "-" + boxNum.getAndIncrement();
                                inbound.setBoxNumber(boxId);
                                String request = DealSTDRequest.pushBoxAnnounceKB(msgNum.getAndIncrement(), boxId, inbound);
                                boolean tcpSend = send(request);
                                int times = 0;
                                if (!tcpSend) {
                                    do {
                                        times++;
                                        log.debug("入库 发送失败并重发");
                                        ThreadUtil.sleep(500);
                                    } while (send(request) && times < 10);
                                }
                                if (times >= 10) {
                                    log.info("pushTaskToMFC, {}次, 发送失败, request: {}", times, request);
                                } else {
                                    log.info("push task barcode: {} to level/location: {}/{} ", boxId, level, inbound.getLocation());
                                }
                                ThreadUtil.sleep(100);
                            }

                        });
                    }
                } else {
                    ThreadUtil.sleep(1000);
                }
            } catch (Exception e) {
                log.info("Lift-[{}], pushTaskGenerate failed close Thread; {}", liftId, e.getMessage());
                break;
            }
        }
    }


    public void sendRequest(Boxlift boxLift) {
        int liftId = boxLift.getId();
        int liftPos = boxLift.getPos();
        log.info("BoxLift-[{}], pos: {}, ip: {}, start...", liftId, liftPos, deviceIp);
        while (true) {
            try {
                if (!autoInbound.isSelected()) {
                    ThreadUtil.sleep(1000);
                    continue;
                }
                List<Boxliftshelfpd> pds = shelfPdService.selectPds(liftPos);
                List<Appointannounce> totalAppoint = appointAnnounceService.isExitAppoint(-1, liftId);
                for (Boxliftshelfpd pd : pds) {
                    int pdLevel = pd.getLevel();
                    if (!autoInbound.isSelected()) {
                        continue;
                    }
                    if (pd.getInboundRequest() == 1 || pd.getInboundState() == 3) {
                        continue;
                    }
                    List<Appointannounce> currentLevelAppoint = totalAppoint.stream().filter(appointannounce -> appointannounce.getLevel().equals(pdLevel)).collect(Collectors.toList());
                    if (currentLevelAppoint.size() == 0) {
                        continue;
                    }
                    Appointannounce appointannounce = currentLevelAppoint.get(0);
                    String boxId = appointannounce.getBoxNumber();
                    //送至提升机
                    String requestMk = DeviceHttpRequest.sendRequest(deviceIp, 1, liftId, 1, boxId);
                    log.info("MK-[{}], request level:{}, barcode:{} {}", liftId, pdLevel, boxId, requestMk);
                    ThreadUtil.sleep(1000);
                    Announce exitAnnounce = announceService.isExitAnnounce(boxId);
                    int times = 0;
                    do {
                        if (times > 0) {
                            log.debug("MK-[{}], box:{} not found on announce, times:{}", liftId, boxId, times);
                            ThreadUtil.sleep(500);
                            exitAnnounce = announceService.isExitAnnounce(boxId);
                        }
                        times++;
                    } while (exitAnnounce == null && times < 10);
                    if (times == 10) {
                        log.info("MK-[{}], send barcode:{}, {}次 ,announce任务未生成,不发了", liftId, boxId, times);
                        continue;
                    }
                    String requestPD = DeviceHttpRequest.sendRequest(deviceIp, 2, pd.getId(), 1, boxId);
                    log.info("PD---[{}], request level:{}, barcode:{} {}", pd.getId(), pdLevel, boxId, requestPD);
                    ThreadUtil.sleep(500);
                }
                ThreadUtil.sleep(1000);
            } catch (Exception e) {
                log.info("Lift-{} request failed close Thread, {}", liftId, e.getMessage());
                break;
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
            if (locations.size() == 0) {
                locations = locationsService.outLocations();
            }
            int times = CommonUtil.getTimes(outboundEveryLevelNums, aisles.size());
            List<Task> totalTasks = taskService.outboundTask(-1, -1);
            for (Integer level : levels) {
                List<Task> currentLevelTask = totalTasks.stream().filter(task -> Objects.equals(task.getSLevel(), level)).collect(Collectors.toList());
                if (currentLevelTask.size() == 0) {
                    List<Locations> currentLevelLocations = locations.stream().filter(lt -> lt.getLevel() == level).collect(Collectors.toList());
                    if (currentLevelLocations.size() > 0) {
                        for (Integer aisle : aisles) {
                            if (!autoOutbound.isSelected()) {
                                return;
                            }
                            List<Locations> currentLevelAndAisleLocations = currentLevelLocations.stream().filter(lt -> lt.getAisle() == aisle).collect(Collectors.toList());
                            if (currentLevelAndAisleLocations.size() > 0) {
                                for (int i = 0; i < times; i++) {
                                    Locations outLocation = CommonUtil.randomFromList(locations);
                                    currentLevelLocations.remove(outLocation);
                                    locations.remove(outLocation);
                                    String boxId = Constance.OUT_BOX_PREFIX + outBoxNum.getAndIncrement();
                                    outLocation.setBoxNumber(boxId);
                                    String request = DealSTDRequest.pushAppointStockOutKB(msgNum.getAndIncrement(), outLocation);
                                    boolean tcpSend = send(request);
                                    int num = 0;
                                    if (!tcpSend) {
                                        do {
                                            num++;
                                            log.debug("发送失败,重发");
                                            ThreadUtil.sleep(500);
                                        } while (send(request) && num < 10);
                                    }
                                    if (num >= 10) {
                                        log.info("超出重发最大次数: request:{}", request);
                                    } else {
                                        log.info("出库 - > level/location:{}/{}, boxId:{}", level, outLocation.getLocation(), boxId);
                                    }
                                    ThreadUtil.sleep(100);
                                }
                            }
                        }
                    }
                }
            }

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
            if (locations.size() <= 10) {
                locations = locationsService.outLocations();
            }
            int times = CommonUtil.getTimes(moveEveryLevelNums, aisles.size());
            List<Task> totalMoveTask = taskService.moveTask(-1);
            levels.forEach(level -> {
                List<Task> currentLevelMoveTask = totalMoveTask.stream().filter(task -> task.getSLevel().equals(level)).collect(Collectors.toList());
                if (currentLevelMoveTask.size() == 0) {
                    aisles.forEach(aisle -> {
                        if (!autoMove.isSelected()) {
                            return;
                        }
                        for (int i = currentLevelMoveTask.size(); i < times; i++) {
                            Locations start = CommonUtil.randomFromList(locations);
                            String boxId = Constance.MOVE_BOX_PREFIX + moveBoxNum.getAndIncrement();
                            start.setBoxNumber(boxId);
                            locations.remove(start);
                            Locations end = CommonUtil.randomFromList(locations);
                            locations.remove(end);
                            String request = DealSTDRequest.pushMoveLibraryKB(msgNum.getAndIncrement(), start, end);
                            boolean tcpSend = send(request);
                            int num = 0;
                            if (!tcpSend) {
                                do {
                                    num++;
                                    log.debug("移库 发送失败并重发");
                                    ThreadUtil.sleep(500);
                                } while (send(request) && num < 10);
                            }
                            if (num >= 10) {
                                log.info("超出重发最大次数: request:{}", request);
                            } else {
                                log.info("移库 - > s_level/s_location: {}/{}, e_level/e_location: {}/{} boxId:{}",
                                        start.getLevel(), start.getLocation(), end.getLevel(), end.getLocation(), boxId);
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