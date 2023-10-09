package com.lee.database.std.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.database.std.entity.Task;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lee
 * @since 2022-06-24
 */
public interface ITaskService extends IService<Task> {

    List<Task> outTask();

    List<Task> outTask(int level);

    //根据提升机pos，初始化boxId
    int initBoxId(int boxLiftPos);

    int initLiftBoxNumByPreStr(String liftBoxPre);

    int initId(int type);

    int initBoxIdSK(int boxLiftPos);

    List<Task> outboundTask(int liftPos);

    List<Task> outboundTask(int level, int liftPos);

    List<Task> moveTask(int liftPos);

    List<Task> moveLibrary(int level);

    List<Task> moveLibrary(int level, int aisle);

    List<Task> isExitTask(int level, int pos);

    List<Task> inboundTask(int level, int liftPos);

    List<Task> inboundLiftTask(int level, int liftPos);

    List<Task> inboundLiftTaskSK(int level, int liftId);
}
