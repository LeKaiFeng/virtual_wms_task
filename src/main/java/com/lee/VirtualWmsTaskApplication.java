package com.lee;

import com.lee.control.MainDssController;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.stage.Stage;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.lee.database.*.mapper")
@SpringBootApplication
@EnableScheduling
public class VirtualWmsTaskApplication extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        launch(VirtualWmsTaskApplication.class, MainDssController.class, args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("virtual wms");
        stage.setOnCloseRequest(event -> {
            System.exit(0);
        });
        super.start(stage);
    }
}
