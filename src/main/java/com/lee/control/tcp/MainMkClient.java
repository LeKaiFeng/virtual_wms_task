package com.lee.control.tcp;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.FXMLView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;


@FXMLController
@FXMLView(value = "/fxml/tcp/mk/mkClient.fxml")
@Component
public class MainMkClient extends AbstractFxmlView implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(MainMkClient.class);
    @FXML
    protected JFXToggleButton autoInbound, autoReq, autoOutbound, autoMove;
    @FXML
    protected TextField ip, port;
    @FXML
    protected JFXButton connectBtn, description;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}