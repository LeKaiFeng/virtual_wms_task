package com.lee.util;


import cn.hutool.core.util.RandomUtil;
import com.lee.database.mk.entity.Locations;
import com.lee.database.mk.entity.Task;

import java.time.LocalDateTime;

/**
 * @Date: 2021/12/22 10:17
 * @Description: TODO
 */
public class MKTaskTemplate {

    /**
     * @param lt
     * @return
     */
    public static Task inboundTask(Locations lt) {

        Task task = new Task();
        task.setPid(0);
        task.setType(1);


        task.setSLevel(lt.getLevel());
        task.setSPos(1);
        task.setSLocation(1);

        task.setELevel(lt.getLevel());
        task.setEPos(lt.getPos());
        task.setELocation(lt.getLocation());

        task.setState(0);
        task.setBoxNumber(lt.getBoxNumber());
        task.setPriority(1);

        task.setTargetFloor(0);
        task.setWidth(0);

        task.setWeight(10);
        task.setWmsid(RandomUtil.randomString(18));
        task.setCreateTime(LocalDateTime.now());

        task.setLiftArea(lt.getLiftArea());
        task.setBoxType("0");
        task.setOrderState(5);
        task.setRunningState(1);
        //task.setSite("1079");
        return task;
    }


}

