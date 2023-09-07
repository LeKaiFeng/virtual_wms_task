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
 * <p>
 * 1. mfc 作 client , wms 作 server 时,
 * mfc发送的updateMovement消息，wms 是用server端回复完成； 还是client端回复完成
 * <p>
 * 2.一条消息请求之后，需不需要等待回复再发下一条; （发完不管则缓存，回复后请缓存）
 * <p>
 * 3. 缓存： ① 内存中，一旦异常数据丢失
 * ② 数据库持久化，a.共用ga_message(id会不会冲突) b.新建virtual_message,各查各的
 * <p>
 * 4.是否需要管货位.....（反正是模拟车，货位状态可管可不管）
 * <p>
 * 5.......好多种消息类型，时序繁琐，工作量大
 * <p>
 * 7.界面化 （后期需不需要整合到模拟器）
 */
public class DealRequest {

    public static final Log log = LogFactory.get();
    public static volatile int id = 1;

    /**
     * TODO 下发出库任务（WMS->MFC）
     * 1.管货位
     * 2.看mfc 回复 result是否为0, 否则....未写完
     */
    public static String pushAppointStockOut(int msgId, Locations location) {
        HashMap<Object, Object> responseMap = new LinkedHashMap<Object, Object>() {
            {
                put("id", msgId);
                put("messageName", "appointStockOut");
                put("boxId", Constance.BOX_PREFIX + msgId);
                put("level", location.getLevel());
                put("location", location.getLocation());
                put("type", 0); //type 0=出库  1=取消（任务未开始）
                put("wmsId", RandomUtil.randomString(10)); //type 0=出库  1=取消（任务未开始）
                put("priority", 1);
                put("outbound", RandomUtil.randomInt(3) + 1);  // 有值时，指定出库口PD的Id；为0时，mfc分配出库口
            }
        };
        return JSONUtil.toJsonStr(responseMap);
    }

    public static String pushAppointStockOutLF(int msgId, Locations location) {
        HashMap<Object, Object> responseMap = new LinkedHashMap<Object, Object>() {
            {
                put("id", msgId);
                put("messageName", "appointStockOut");
                put("boxId", Constance.BOX_PREFIX + msgId);
                put("level", location.getLevel());
                put("location", location.getLocation());
                put("type", 0); //type 0=出库  1=取消（任务未开始）
                put("wmsId", RandomUtil.randomString(10)); //type 0=出库  1=取消（任务未开始）
                put("priority", 1);
                put("outbound", 0);  // 有值时，指定出库口PD的Id；为0时，mfc分配出库口
            }
        };
        return JSONUtil.toJsonStr(responseMap);
    }

    /**
     * TODO 入库任务送至层（WMS->MFC)
     */
    public static String pushAppointBoxAnnounce(int megId, int level, String boxId) {
        HashMap<Object, Object> responseMap = new LinkedHashMap<Object, Object>() {
            {
                put("id", megId);
                put("messageName", "appointBoxAnnounce");
                //put("boxId", Constance.BOX_PREFIX + megId);
                put("boxId", boxId);
                put("level", level);
                put("weight", 20);
                put("wmsId", RandomUtil.randomString(10)); //type 0=出库  1=取消（任务未开始）
                put("outbound", 0);  // 为0时，指定入库层；，有值时mfc提出
            }
        };
        return JSONUtil.toJsonStr(responseMap);
    }


    public static String pushAppointBoxAnnounceLF(int megId, int level, int location, String boxId) {
        HashMap<Object, Object> responseMap = new LinkedHashMap<Object, Object>() {
            {
                put("id", megId);
                put("messageName", "appointBoxAnnounce");
                //put("boxId", Constance.BOX_PREFIX + megId);
                put("boxId", boxId);
                put("level", level);
                put("location", location);
                put("weight", 20);
                put("type", 0);
                put("wmsId", RandomUtil.randomString(10)); //type 0=出库  1=取消（任务未开始）
                put("outbound", 0);  // 为0时，指定入库层；，有值时mfc提出
            }
        };
        return JSONUtil.toJsonStr(responseMap);
    }


    /**
     * TODO 预约入库（WMS->MFC)  飞策
     */
    public static String pushBoxAnnounce(int megId, String area) {
        HashMap<Object, Object> responseMap = new LinkedHashMap<Object, Object>() {
            {
                put("id", megId);
                put("messageName", "boxAnnounce");
                put("type", 0);
                put("boxId", Constance.BOX_PREFIX + megId);
                put("weight", 20);
                put("wmsId", RandomUtil.randomString(10)); //type 0=出库  1=取消（任务未开始）
                put("area", area);
                put("outbound", 0);
            }
        };
        return JSONUtil.toJsonStr(responseMap);
    }


    /**
     * TODO 出库任务（WMS->MFC)  飞策
     */
    public static String pushStockOut(int megId, String boxId, int deviceId) {
        HashMap<Object, Object> responseMap = new LinkedHashMap<Object, Object>() {
            {
                put("id", megId);
                put("messageName", "StockOut");
                put("boxId", boxId);
                put("wmsId", RandomUtil.randomString(10)); //type 0=出库  1=取消（任务未开始）
                put("priority", "1");
                put("type", 20);
                put("outbound", deviceId);
            }
        };
        return JSONUtil.toJsonStr(responseMap);
    }


    /**
     * 接收到 updateMovement 搬运信息
     * 就是让 WMS 管货位的
     */
    public static String requestUpdateMovement() {
        /*TreeMap 有序 */
        // SortedMap<Object, Object> locationMap = new TreeMap<Object, Object>() {
        // private static final long serialVersionUID = 1L;
        HashMap<Object, Object> locationMap = new LinkedHashMap<Object, Object>() {
            {
                put("s_level", RandomUtil.randomInt(10) + 1);
                put("s_location", 110111);
                put("e_level", RandomUtil.randomInt(10) + 1);
                put("e_location", 110118);
                put("wmsId", RandomUtil.randomNumbers(8));
                put("r_level", 0);
                put("r_location", 0);
            }
        };

        HashMap<Object, Object> responseMap = new LinkedHashMap<Object, Object>() {
            {
                put("id", id++);
                put("messageName", "updateMovement");
                put("boxId", RandomUtil.randomString(4));
                put("wmsId", RandomUtil.randomNumbers(8));
                put("type", 1);
                put("location", locationMap);
                put("state", 0);
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

                put("location", locations.getLocation());    //TODO 按指定巷道找位置
                put("area", locations.getLiftArea());        //TODO 哪里查

                put("wmsId", requestJson.getStr("wmsId"));
                put("outbound", outbound);  //0=入到wms指定的位置  1=让 mfc 踢出
            }
        };
        return JSONUtil.toJsonStr(responseMap);
    }

    public static String pushAppointBoxAnnounce(String request, Locations location) {

        JSONObject requestJson = JSONUtil.parseObj(request);

        HashMap<Object, Object> responseMap = new LinkedHashMap<Object, Object>() {
            {
                put("id", requestJson.getInt("id"));
                put("messageName", "appointBoxAnnounce");
                put("boxId", requestJson.getStr("boxId"));
                put("location", location.getLevel() + "-" + location.getLocation());
                put("wmsId", RandomUtil.randomString(16));
                put("site", 0);
                put("boxtype", RandomUtil.randomInt(2));
                put("isClipBox", 0);
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
                put("boxId", Constance.BOX_PREFIX + msgId);
                put("s_level", start.getLevel());
                put("s_location", start.getLocation());
                put("e_level", end.getLevel());
                put("e_location", end.getLocation());
                put("wmsId", RandomUtil.randomString(12));
                put("priority", 1);

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
