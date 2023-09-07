package com.lee.control.tcp;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.FXMLView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @Date: 2022/12/6 9:05
 * @Description: TODO
 */

@FXMLController
@FXMLView(value = "/fxml/tcp/tcpMain.fxml")
public class MainTCPController extends AbstractFxmlView implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
