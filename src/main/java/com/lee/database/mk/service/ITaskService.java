package com.lee.database.mk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.database.mk.entity.Task;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lee
 * @since 2022-06-28
 */
public interface ITaskService extends IService<Task> {


    List<Task> selectTask(int type, int state, String barcode);

    /**
     * 输送线上的箱子到达入库口1079
     *
     * @return
     */
    List<Task> lineTask();

    List<Task> outboundTaskLF(int level, int aisle);

    Task inboundTaskState1(int level, int liftId);

    int initTaskIdByType(int type);

    int initTaskIdByLift(int liftPos);

    List<Task> inboundTask();

    List<Task> inboundTaskByLift(int liftId);

    List<Task> inboundTaskGroupByLevel(int liftId, String site);

    List<Task> outboundTask(int level);

    List<Task> moveTask(int level);

}
