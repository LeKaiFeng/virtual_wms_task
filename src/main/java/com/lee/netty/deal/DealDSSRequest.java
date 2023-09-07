package com.lee.netty.deal;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.lee.database.dss.entity.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Author: Lee
 * @Date: Created in 13:39 2022/6/19
 * @Description: TODO 恒昌医药
 */
public class DealDSSRequest {

    public static final Log log = LogFactory.get();

    public static String pushTaskGenerate(int type, int megId, int liftId, String boxId, ResourceLocation sLocations, ResourceLocation eLocations) {
        HashMap<Object, Object> responseMap = new LinkedHashMap<Object, Object>() {
            {
                put("id", megId);
                put("type", "taskGenerate");
                if (type == 1) {
                    put("body", createDslInboundTask(boxId, liftId, eLocations));
                } else if (type == 2) {
                    put("body", createDslOutboundTask(boxId, sLocations));
                } else if (type == 15) {
                    put("body", createDslMoveTask(boxId, sLocations, eLocations));
                }
            }
        };
        return JSONUtil.toJsonStr(responseMap);
    }

    public static HashMap<Object, Object> createInboundTask(String boxId, ResourceLocation locations) {
        HashMap<Object, Object> task = new LinkedHashMap<Object, Object>() {
            {
                put("wmsId", RandomUtil.randomString(8));
                put("taskType", 1);
                put("barcode", boxId);
                put("level", locations.getLevel());
                put("endLocation", locations.getLocation());
            }
        };
        return fixTaskList(task);
    }

    public static HashMap<Object, Object> createDslInboundTask(String boxId, int mkId, ResourceLocation endLocation) {
        HashMap<Object, Object> task = new LinkedHashMap<Object, Object>() {
            {
                put("wmsId", RandomUtil.randomString(12));
                put("taskType", 1);
                put("barcode", boxId);
                put("level", endLocation.getLevel());
                put("endLocation", endLocation.getLocation());
                put("mkId", mkId);
                put("boxType", RandomUtil.randomInt(3) + 1);
                put("width", (RandomUtil.randomInt(4) + 3) * 100); // 0,1,2,3-> 3,4,5,6
                put("length", (RandomUtil.randomInt(5) + 3) * 100); //0,1,2,3,4-> 3,4,5,6,7
                put("height", (RandomUtil.randomInt(4) + 1) * 100);
                put("weight", (RandomUtil.randomInt(5) + 1) * 10);
            }
        };
        return fixTaskList(task);
    }

    public static HashMap<Object, Object> createOutboundTask(String boxId, ResourceLocation locations) {
        HashMap<Object, Object> task = new LinkedHashMap<Object, Object>() {
            {
                put("wmsId", RandomUtil.randomString(8));
                put("taskType", 2);
                put("barcode", boxId);
                put("level", locations.getLevel());
                put("startLocation", locations.getLocation());
                put("outLevel", 1);
                put("priority", 1100);
            }
        };

        return fixTaskList(task);
    }

    public static HashMap<Object, Object> createDslOutboundTask(String boxId, ResourceLocation locations) {
        HashMap<Object, Object> task = new LinkedHashMap<Object, Object>() {
            {
                put("wmsId", RandomUtil.randomString(12));
                put("taskType", 2);
                put("barcode", boxId);
                put("level", locations.getLevel());
                put("startLocation", locations.getLocation());
                put("mkId", RandomUtil.randomInt(6) + 1);
                put("boxType", RandomUtil.randomInt(3) + 1);
                put("width", (RandomUtil.randomInt(4) + 3) * 100); // 0,1,2,3-> 3,4,5,6
                put("length", (RandomUtil.randomInt(5) + 3) * 100); //0,1,2,3,4-> 3,4,5,6,7
                put("height", (RandomUtil.randomInt(4) + 1) * 100);
                put("weight", (RandomUtil.randomInt(5) + 1) * 10);
                put("outLevel", RandomUtil.randomInt(3) + 1);
                put("priority", 1100);
            }
        };

        return fixTaskList(task);
    }

    /**
     * 优先做
     * 每1min生成1条且当前层没移库
     *
     * @param boxId
     * @param sLocations
     * @param eLocations
     * @return
     */
    public static HashMap<Object, Object> createMoveTask(String boxId, ResourceLocation sLocations, ResourceLocation eLocations) {
        HashMap<Object, Object> task = new LinkedHashMap<Object, Object>() {
            {
                put("wmsId", RandomUtil.randomString(8));
                put("taskType", 15);
                put("barcode", boxId);
                put("level", sLocations.getLevel());
                put("startLocation", sLocations.getLocation());
                put("endLocation", eLocations.getLocation());
                put("priority", 10010);
            }
        };
        return fixTaskList(task);
    }

    public static HashMap<Object, Object> createDslMoveTask(String boxId, ResourceLocation sLocations, ResourceLocation eLocations) {
        HashMap<Object, Object> task = new LinkedHashMap<Object, Object>() {
            {
                put("wmsId", RandomUtil.randomString(8));
                put("taskType", 15);
                put("barcode", boxId);
                put("level", sLocations.getLevel());
                put("startLocation", sLocations.getLocation());
                put("endLocation", eLocations.getLocation());
                put("boxType", RandomUtil.randomInt(3) + 1);
                put("width", (RandomUtil.randomInt(4) + 3) * 100); // 0,1,2,3-> 3,4,5,6
                put("length", (RandomUtil.randomInt(5) + 3) * 100); //0,1,2,3,4-> 3,4,5,6,7
                put("height", (RandomUtil.randomInt(4) + 1) * 100);
                put("weight", (RandomUtil.randomInt(5) + 1) * 10);
                put("priority", 1001);
            }
        };
        return fixTaskList(task);
    }


    public static HashMap<Object, Object> fixTaskList(HashMap<Object, Object> task) {
        List<Object> taskList = new ArrayList<Object>() {
            {
                add(task);
            }
        };
        HashMap<Object, Object> taskJson = new LinkedHashMap<Object, Object>() {
            {
                put("taskList", taskList);
            }
        };
        return taskJson;
    }
}
