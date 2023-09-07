package com.lee.control.tcp;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.FXMLView;
import javafx.fxml.Initializable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;

import java.net.URL;
import java.util.*;

/**
 * @Date: 2022/12/6 9:05
 * @Description: TODO
 */
@FXMLController
@FXMLView(value = "/fxml/tcp/tcpClientSiYuan.fxml")
@ConditionalOnProperty(name = "project.name", havingValue = "SiYuan")
public class MainTCPClientSiYuanController extends AbstractFxmlView implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public volatile int id = 1;

    @Scheduled(fixedRateString = "${strategy.outbound.intervalTime}", initialDelay = 1000 * 3)
    // 延时10s启动，之后每5s执行一次
    public void outboundTask() {
        System.out.println(id++);
    }
}