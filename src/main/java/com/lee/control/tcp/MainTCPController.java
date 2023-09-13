package com.lee.control.tcp;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.FXMLView;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

@FXMLController
@FXMLView(value = "/fxml/tcp/tcpMain.fxml")
public class MainTCPController extends AbstractFxmlView implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //这里可以干点什么....
    }
}
