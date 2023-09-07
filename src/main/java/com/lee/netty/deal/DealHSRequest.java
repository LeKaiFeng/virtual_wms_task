package com.lee.netty.deal;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.lee.database.mk.entity.Locations;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @Author: Lee
 * @Date: Created in 13:39 2022/6/19
 * @Description: TODO 消息回复，WMS做client, 主动下发消息并等待回复
 */
public class DealHSRequest {

    public static final Log log = LogFactory.get();
    public static volatile int id = 1;

    public static String pushBoxAnnounce(int megId, Locations locations) {
        HashMap<Object, Object> responseMap = new LinkedHashMap<Object, Object>() {
            {
                put("id", megId);
                put("messageName", "appointBoxAnnounce");
                put("boxId", locations.getBoxNumber());
                put("level", locations.getLevel());
                put("location", locations.getLocation());
                put("weight", 20);
                put("wmsId", RandomUtil.randomString(10));
                put("box_type", "S");
                put("outbound", 0);
            }
        };
        return JSONUtil.toJsonStr(responseMap);
    }


    public static String pushAppointStockOut(int megId, Locations locations) {
        HashMap<Object, Object> responseMap = new LinkedHashMap<Object, Object>() {
            {
                put("id", megId);
                put("messageName", "appointStockOut");
                put("boxId", locations.getBoxNumber());
                put("level", locations.getLevel());
                put("location", locations.getLocation());
                put("type", 0);
                put("wmsId", RandomUtil.randomString(10)); //type 0=出库  1=取消（任务未开始）
                put("priority", "1");
                put("box_type", "S");
                put("outbound", 0);
            }
        };
        return JSONUtil.toJsonStr(responseMap);
    }


    /**
     * todo 移库任务（WMS -> MFC）
     * 同层移库
     */
    public static String pushMoveLibrary(int msgId, Locations start, Locations end) {

        HashMap<Object, Object> responseMap = new LinkedHashMap<Object, Object>() {
            {
                put("id", msgId);
                put("messageName", "moveLibrary");
                put("boxId", start.getBoxNumber());
                put("s_level", start.getLevel());
                put("s_location", start.getLocation());
                put("e_level", end.getLevel());
                put("e_location", end.getLocation());
                put("wmsId", RandomUtil.randomString(10));
                put("box_type", "S");
                put("priority", 1);

            }
        };
        return JSONUtil.toJsonStr(responseMap);
    }


}
