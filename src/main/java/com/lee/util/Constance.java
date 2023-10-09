package com.lee.util;

public interface Constance {


    String DSS = "dss";
    String OES = "oes";
    String MK = "mk";
    String STD = "std";
    String EMPTY = "";
    int HTTP_PORT = 8090;
    String BOX_PREFIX = "BoxLift-";
    String OUT_BOX_PREFIX = "OUTBOUND-";
    String MOVE_BOX_PREFIX = "MOVE-";

    String URL = "http://127.0.0.1:8080/GSEE/receiveTask";


    //Setting SETTING = new Setting(System.getProperty("user.dir") + "/application.propertis");
    //double SLEEP_COEFFICIENT = SETTING.getDouble("sleep_coefficient", 0.3D);

    static String getURL(String ip) {
        return String.format("http://%s:8080/GSEE/receiveTask", ip);
    }


}
