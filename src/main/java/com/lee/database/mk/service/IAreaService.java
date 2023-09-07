package com.lee.database.mk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.database.mk.entity.Area;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lee
 * @since 2022-06-28
 */
public interface IAreaService extends IService<Area> {

    List<Area> areas(int level);

    List<Integer> getAisles(int level, int car_group, boolean isExitArea);

    //查询当前巷道所在分区
    int getGroup(int level, int aisle);
}
