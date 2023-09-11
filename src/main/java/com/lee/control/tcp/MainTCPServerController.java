package com.lee.control.tcp;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jfoenix.controls.JFXButton;
import com.lee.database.mk.entity.Locations;
import com.lee.database.mk.service.impl.LocationsServiceImpl;
import com.lee.netty.NettyServer;
import com.lee.netty.NettyServerHandler;
import com.lee.netty.deal.DealRequest;
import com.lee.util.CommonUtil;
import com.lee.util.ThreadUtil;
import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.FXMLView;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringEncoder;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import io.netty.handler.codec.string.StringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @Date: 2022/12/6 9:05
 * @Description: TODO
 */
@FXMLController
@FXMLView(value = "/fxml/tcp/tcpServer.fxml")
public class MainTCPServerController extends AbstractFxmlView implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(MainTCPClientController.class);

    @FXML
    protected TextField port;
    @FXML
    protected JFXButton startBtn;

    public NettyServer nettyServer;

    @Value("${server.WMS.port}")
    public String wmsPort;
    @Value("${server.WMS.end}")
    public String wmsEnd;

    @Autowired
    private LocationsServiceImpl locationsService;

    protected List<Integer> aisles;
    protected List<Integer> levels;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

//        ThreadUtil.execute(() -> {
//            aisles = locationsService.allAisles();
//            levels = locationsService.allLevels();
//        });

        port.setText(wmsPort);
        startBtn.setOnMouseClicked(event -> {
            openServer();
        });
    }


    public void openServer() {
        Integer portValue = Convert.toInt(port.getText());

        nettyServer = new NettyServer(portValue, new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast(new LineBasedFrameDecoder(1024))
                        .addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer(wmsEnd.getBytes())))
                        .addLast(new StringDecoder())
                        .addLast(new StringEncoder())
                        //.addLast(new Base64Encoder())
                        //.addLast(new Base64Decoder())
                        .addLast("serverHandler", new NettyServerHandler());
            }
        });
        ThreadUtil.createThread("wmsServer - init", nettyServer::init).start();
        port.setDisable(true);
        startBtn.setDisable(true);
    }


    //    @Scheduled(initialDelay = 1000, fixedDelay = 100)
    public void dealBoxAnnounceOrSupplement() {
        Map<Integer, String> recAnnounceMap = NettyServerHandler.cacheBoxAnnounceMap;
        try {
            if (recAnnounceMap.size() != 0) {
                for (Map.Entry<Integer, String> entry : recAnnounceMap.entrySet()) {
                    //System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
                    String body = entry.getValue();
                    JSONObject bodyJson = JSONUtil.parseObj(body);

                    String boxId = bodyJson.getStr("boxId");

                    Integer level = CommonUtil.randomFromList(levels);
                    Integer aisle = CommonUtil.randomFromList(aisles);
                    if (level == null || aisle == null) {
                        return;
                    }
                    //乱分
                    List<Locations> locations = locationsService.inbound(level, aisle);
                    if (locations.size() == 0) {
                        log.info("level:{} aisles:{} 无可用货位", level, aisle);
                        continue;
                    }
                    Locations location = locations.get(0);
                    location.setBoxNumber(boxId);
                    String response = DealRequest.pushAppointBoxAnnounce(body, location);
                    NettyServerHandler.write(response + "\r\n");
                    log.info("server send:{}", response);
                    //占用货位
                    int occupy = locationsService.occupy(location);
                    if (occupy == 1) {
                        log.info("预占用 level:{} location:{} boxId:{} {}", level, location.getLocation(), boxId, "success");
                    }
                    NettyServerHandler.cacheBoxAnnounceMap.remove(entry.getKey());
                    ThreadUtil.sleep(100);
                    log.info("clean cache:{}", body);

                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }

    }


}
