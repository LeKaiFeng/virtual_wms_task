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
import com.lee.database.std.entity.Announce;
import com.lee.database.std.entity.Boxlift;
import com.lee.database.std.entity.Boxliftshelfpd;
import com.lee.database.std.entity.Locations;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ThreadUtil.execute(this::initId);
        boxLiftService.query().select("id", "pos", "ip").list().forEach(boxLift -> {
            log.info("boxLift-{} thread start", boxLift.getId());
            lifts.add(boxLift);
//            ThreadUtil.createThread("TaskGenerate" + boxLift.getId(), () -> pushTaskGenerate(boxLift)).start();
//            ThreadUtil.createThread("boxLift" + boxLift.getId(), () -> sendRequest(boxLift)).start();
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
        List<Boxliftshelfpd> pds = shelfPdService.selectPds(liftPos);
        while (true) {
            try {
                if (autoInbound.isSelected()) {
                    List<Announce> exitAnnounce = announceService.isExitAnnounce(liftId);
                    if (exitAnnounce.size() == 0) {
                        List<Locations> locations = locationsService.inboundLocations(-1);
                        levels.forEach(level -> {
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
                                    log.info("入库 发送失败并重发");
                                    ThreadUtil.sleep(500);
                                } while (send(request) && times < 10);
                            }
                            if (times == 10) {
                                log.info("{}次重发未成功,request:{}", times, request);
                            }
                            ThreadUtil.sleep(100);
                            log.info("push task barcode: {} to level/location: {}/{} ", boxId, level, inbound.getLocation());
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