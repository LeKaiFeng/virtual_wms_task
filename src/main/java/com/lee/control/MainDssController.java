package com.lee.control;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.FXMLView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

@FXMLController
@FXMLView(value = "/fxml/mainDss.fxml")
public class MainDssController extends AbstractFxmlView implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(MainDssController.class);
//    @Value("${server.version}")
//    protected String version;
//    @Value("${server.project}")
//    protected String project;
//    @Value("${server.connType}")
//    protected String connType;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void addTab(Tab t, String name) {
        String filePath = "";
        try {
            filePath = getFilePath();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(filePath)));
            t.setContent(root);
//            tp.getTabs().add(new Tab(name, root));
        } catch (IOException e) {
            log.info("name: {}, filePath: {} error ->> {}", name, filePath, e.getMessage());
        }
    }

    public String getFilePath() {
        String filePath = "";
//        String special = project.equalsIgnoreCase(version) ? "" : project;
//        switch (connType) {
//            case "tcp":
//                filePath = String.format("/fxml/client/%s/%s%sClient.fxml", version, version, special);
//                break;
//            case "http":
//                filePath = String.format("/fxml/http/%s/%s%sClient.fxml", version, version, special);
//                break;
//            default:
//                log.info("connType:{}, 配置错误,必须小写", connType);
//        }
        return filePath;
    }
}
