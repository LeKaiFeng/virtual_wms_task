package com.lee.util;


import cn.hutool.core.util.RandomUtil;
import com.lee.database.std.entity.Appointannounce;
import com.lee.database.std.entity.Locations;
import com.lee.database.std.entity.Task;

import java.time.LocalDateTime;

/**
 * @Date: 2021/12/22 10:17
 * @Description: TODO
 */
public class STDTaskTemplate {

    /**
     * 适用 HG SK 两向车出库
     *
     * @param lt
     * @return
     */
    public static Task outboundTask(Locations lt) {

        Task task = new Task();
        task.setType(2);

        task.setSLevel(lt.getLevel());
        task.setSPos(lt.getPos());
        task.setSLocation(lt.getLocation());

        task.setELevel(100);
        task.setEPos(1);
        task.setELocation(1);

        task.setStatus(0);
        task.setBoxNumber(lt.getBoxNumber());
        task.setPriority(110);

        task.setTargetFloor(1);
        task.setWidth(0);

        task.setWeight(10);
        task.setWmsid(RandomUtil.randomString(18));
        task.setCreateTime(LocalDateTime.now());

        task.setLiftArea(lt.getLiftArea());
        task.setBoxType("S");
        //task.setOrderState(5);
        //task.setRunningState(1);
        return task;
    }

    public static Appointannounce appointAnnounceTask(Locations lt) {

        Appointannounce task = new Appointannounce();

        task.setBoxNumber(lt.getBoxNumber());
        task.setWmsid(RandomUtil.randomString(18));
        task.setState(0);
        //task.setWeight(20);
        task.setLevel(lt.getLevel());
        task.setLocation(lt.getLocation());

        //task.setAnnounceTime(LocalDateTime.now());
        //task.setWidth(0);
        //task.setBoxType("S");
        //task.setTargetFloor(0);

        return task;
    }


    public static Task moveTask(Locations start, Locations end) {

        Task task = new Task();
        task.setType(15);

        task.setSLevel(start.getLevel());
        task.setSPos(start.getPos());
        task.setSLocation(start.getLocation());

        task.setELevel(end.getLevel());
        task.setEPos(end.getPos());
        task.setELocation(end.getLocation());

        task.setStatus(0);
        task.setBoxNumber(start.getBoxNumber());
        task.setPriority(1);

        task.setTargetFloor(1);
        task.setWidth(0);

        task.setWeight(10);
        task.setWmsid(RandomUtil.randomString(18));
        task.setCreateTime(LocalDateTime.now());

        task.setLiftArea(start.getLiftArea());
        task.setBoxType("S");
        task.setOrderState(5);
        task.setRunningState(1);
        return task;
    }

}

