package com.lee.netty.deal;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.lee.database.mk.entity.Locations;
import com.lee.util.Constance;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @Author: Lee
 * @Date: Created in 13:39 2022/6/19
 * @Description: TODO 消息回复，WMS做client, 主动下发消息并等待回复
 */
public class DealRequestCommon {

    public static final Log log = LogFactory.get();

    /**
     * TODO 入库任务送至层（WMS->MFC)
     */
    public static String pushAppointBoxAnnounce(int megId, Locations location) {
        HashMap<Object, Object> responseMap = new LinkedHashMap<Object, Object>() {
            {
                put("id", megId);
                put("messageName", "appointBoxAnnounce");
                //put("boxId", Constance.BOX_PREFIX + megId);
                put("boxId", location.getBoxNumber());
                put("level", location.getLevel());
                put("location", location.getLocation());
                put("weight", 20);
                put("wmsId", RandomUtil.randomString(10));
                put("outbound", 0);  // 为0时，指定入库层；，有值时mfc提出
            }
        };
        return JSONUtil.toJsonStr(responseMap);
    }


    /**
     * TODO 入库任务送至层（WMS->MFC)
     */
    public static String pushAppointBoxAnnounceAsync(int megId, Locations location) {
        HashMap<Object, Object> responseMap = new LinkedHashMap<Object, Object>() {
            {
                put("id", megId);
                put("messageName", "appointBoxAnnounce");
                //put("boxId", Constance.BOX_PREFIX + megId);
                put("boxId", location.getBoxNumber());
                put("level", location.getLevel());
                //put("location", location.getLocation());  //异步
                put("weight", 20);
                put("wmsId", RandomUtil.randomString(10));
                put("outbound", 0);  // 为0时，指定入库层；，有值时mfc提出
            }
        };
        return JSONUtil.toJsonStr(responseMap);
    }


    /**
     * TODO 下发出库任务（WMS->MFC）
     */
    public static String pushAppointStockOut(int msgId, Locations location) {
        int value1 = RandomUtil.randomInt(3);
        int value2 = RandomUtil.randomInt(2) + 1;
        HashMap<Object, Object> responseMap = new LinkedHashMap<Object, Object>() {
            {
                put("id", msgId);
                put("messageName", "appointStockOut");
                put("boxId", location.getBoxNumber());
                put("level", location.getLevel());
                put("location", location.getLocation());
                put("type", 0);                                         //type 0=出库  1=取消（任务未开始）
                put("wmsId", RandomUtil.randomString(10));      //type 0=出库  1=取消（任务未开始）
                put("priority", 1);
                //put("outbound", RandomUtil.randomInt(3) + 1);  // 有值时，指定出库口PD的Id；为0时，mfc分配出库口
                //put("outbound", (value2 == 1) ? 1 : value1);
                put("outbound", 0);
                put("target_floor", value2);
            }
        };
        return JSONUtil.toJsonStr(responseMap);
    }

    public static String pushAppointStockOutJQYF(int msgId, Locations location) {
        HashMap<Object, Object> responseMap = new LinkedHashMap<Object, Object>() {
            {
                put("id", msgId);
                put("messageName", "appointStockOut");
                put("boxId", location.getBoxNumber());
                put("location", location.getLevel() + "-" + location.getLocation());
                put("wmsId", RandomUtil.randomString(16));
                put("priority", 1);
                put("boxtype", RandomUtil.randomInt(2));
                put("site", 1040);
            }
        };
        return JSONUtil.toJsonStr(responseMap);
    }


    /**
     * todo 移库任务（WMS -> MFC）
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
                put("priority", 1);
            }
        };
        return JSONUtil.toJsonStr(responseMap);
    }

    public static String pushMoveLibraryJQYF(int msgId, Locations start, Locations end) {

        HashMap<Object, Object> responseMap = new LinkedHashMap<Object, Object>() {
            {
                put("id", msgId);
                put("messageName", "moveLibrary");
                put("boxId", start.getBoxNumber());
                put("s_location", start.getLevel() + "-" + start.getLocation());
                put("e_location", end.getLevel() + "-" + end.getLocation());
                put("boxtype", RandomUtil.randomInt(2));
                put("wmsId", RandomUtil.randomString(16));
                put("priority", 1);
                put("isClipBox", 0);
            }
        };
        return JSONUtil.toJsonStr(responseMap);
    }


    /**
     * Todo 上位异步发送入库任务货位（WMS -> MFC）
     * 根据 requestLocation 返回 appointLocation , 并锁定货位
     */
    public static String pushAppointLocation(String request, Locations locations, int outbound) {

        JSONObject requestJson = JSONUtil.parseObj(request);

        HashMap<Object, Object> responseMap = new LinkedHashMap<Object, Object>() {
            {
                put("id", requestJson.getInt("id"));
                put("messageName", "appointLocation");
                put("taskId", requestJson.getInt("taskId"));
                put("boxId", requestJson.getStr("boxId"));
                put("level", requestJson.getInt("level"));
                if (locations != null) {
                    put("location", locations.getLocation());    //TODO 按指定巷道找位置
                    put("area", locations.getLiftArea());        //TODO 哪里查
                }
                put("wmsId", requestJson.getStr("wmsId"));
                put("outbound", outbound);  //0=入到wms指定的位置  1=让 mfc 踢出
            }
        };
        return JSONUtil.toJsonStr(responseMap);
    }


    /**
     * todo  提升机出入库切换请求（WMS -> MFC）
     */
    public static String pushChangeMode(int msgId, int liftId, int mode) {
        HashMap<Object, Object> requestMap = new LinkedHashMap<Object, Object>() {
            {
                put("id", msgId);
                put("messageName", "changeMode");
                put("boxLift", liftId);
                put("mode", mode);  //mode  1入库  2出库
            }
        };


        return JSONUtil.toJsonStr(requestMap);
    }


}
