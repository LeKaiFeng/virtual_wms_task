package com.lee.netty.deal;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.lee.database.mk.service.impl.LocationsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Author: Lee
 * @Date: Created in 13:39 2022/6/19
 * @Description: TODO 消息回复，WMS做Server, 对收到的消息做相对应的回复
 */
public class DealResponse {

    public static final Log log = LogFactory.get();


    public static String response(String requestJson) {
        if (JSONUtil.isTypeJSON(requestJson)) {
            JSONObject jsonObject = JSONUtil.parseObj(requestJson);
            String messageName = Convert.toStr(jsonObject.get("messageName"));
            switch (messageName) {
                case "updateMovement":
                    return responseUpdateMovement(requestJson);
                case "requestLocation":
                    return responseRequestLocation(requestJson);
                case "taskAdjust":
                    return responseTaskAdjust(requestJson);
                case "equipmentStatus":
                    return responseEquipmentStatus(requestJson);
                case "changeModeSuccess":
                    return responseChangeModeSuccess(requestJson);
                case "BoxAnnounceOrSupplement":
                    return "BoxAnnounceOrSupplement";
                default:
                    log.info("未知 message: {}", requestJson);
            }
        }
        log.info("illegal json msg: {}", requestJson);
        return "error message";
    }


    public static String responseUpdateMovement(String msg) {
        /**
         * TODO 搬运信息（MFC->WMS）
         * 管货位 未写完...
         * {"id":3,
         * "messageName":"updateMovement",
         * "boxId":"2jpl",
         * "wmsId":"44198064",
         * "type":1,    //todo  1 入库  2 出库  15移库
         * "location":
         *          {"s_level":7,"s_location":110111,
         *          "e_level":10,"e_location":110118,
         *          "wmsId":"59633212",
         *          "r_level":0,"r_location":0}, //todo 货位被占用重新分配时才有值，无占用默认0
         *  "state":0     //todo state  0=创建，1=分配，2=执行中，3=完成，(4=取消，5=执行中WCS断电而异常取消,12=因空取货而取消)
         *  }
         */

        return commonResponse(msg);

    }

    public static String responseRequestLocation(String msg) {
        /**
         * TODO 入库任务请求货位（MFC->WMS）
         * 异步... 让client发送入库货位 （这种消息应该要写到数据库了）
         * 在client 还没给入库货位appointLocation消息前, 我做为Server result给0 ？？？
         */
        return commonResponse(msg);
    }

    @Autowired
    private LocationsServiceImpl locationsService;

    public static String responseTaskAdjust(String msg) {
        /**
         *  TODO  入库任务调整（MFC->WMS）
         */
        JSONObject adjustJson = JSONUtil.parseObj(msg);

        HashMap<Object, Object> adjustMap = new LinkedHashMap<Object, Object>() {
            {
                JSONArray allowLevel = adjustJson.getJSONArray("allowLevel");
                Object[] objects = allowLevel.toArray();
                int level = Convert.toInt(objects[RandomUtil.randomInt(objects.length)]);
                List<Entity> list;
                try {
                    list = Db.use().query("SELECT * FROM `ga_locations` where level= ?", level);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                if (list.size() == 0) {
                    put("level", adjustJson.getInt("level"));
                    put("location", "");//未写完...
                    put("boxId", adjustJson.getStr("boxId"));
                    put("outbound", 1);  //0 指具体货位; 不为0，踢出
                } else {
                    Entity entity = list.get(RandomUtil.randomInt(list.size()));
                    put("level", entity.getInt("level"));
                    put("location", entity.getInt("location"));//未写完...
                    put("boxId", adjustJson.getStr("boxId"));
                    put("outbound", 0);  //0 指具体货位; 不为0，踢出
                }


            }
        };

        HashMap<Object, Object> responseMap = new LinkedHashMap<Object, Object>() {
            {
                put("id", adjustJson.getInt("id"));
                put("messageName", adjustJson.getStr("messageName"));
                put("wmsId", adjustJson.getStr("wmsId"));
                put("result", adjustMap);
            }
        };
        return JSONUtil.toJsonStr(responseMap);

    }

    public static String responseEquipmentStatus(String msg) {
        //TODO  设备状态主动上报（MFC->WMS）
        return commonResponse(msg);
    }


    public static String responseChangeModeSuccess(String msg) {
        //TODO  提升机出入库切换成功上报（MFC->WMS）
        return commonResponse(msg);
    }

    public static String commonResponse(String requestJson) {
        JSONObject entries = JSONUtil.parseObj(requestJson);
        HashMap<Object, Object> commonMap = new LinkedHashMap<Object, Object>() {
            {
                put("id", entries.getInt("id"));
                String messageName = entries.getStr("messageName");
                put("messageName", messageName);
                if (!"changeModeSuccess".equalsIgnoreCase(messageName) && !"equipmentStatus".equalsIgnoreCase(messageName)) {
                    put("wmsId", entries.getStr("wmsId"));
                }
                put("result", 0);
            }
        };
        return JSONUtil.toJsonStr(commonMap);
    }

}
