package com.lee.database.dss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.database.dss.entity.ResourceTask;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lee
 * @since 2022-08-31
 */
public interface IResourceTaskService extends IService<ResourceTask> {

    int initTaskIdByType(int type);

    int initBoxId(int boxLiftPos);

    List<ResourceTask> searchOutboundTask(int level, List<Integer> aisle);

    List<ResourceTask> searchInboundTask(int level, List<Integer> aisles);

    List<ResourceTask> getLiftInboundTask(int level, int liftId);

    List<ResourceTask> searchMoveTask(int level, List<Integer> aisles);

    ResourceTask isOnLiftTask(String barcode, int level);

    ResourceTask isExitTask(int level, String boxId);
}
