package com.lee.database.dss.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.database.dss.entity.RunningArea;
import com.lee.database.mk.entity.Area;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lee
 * @since 2022-09-01
 */
public interface IRunningAreaService extends IService<RunningArea> {

    List<RunningArea> groupSize(int level);

    List<RunningArea> sameGroupAisle(int level, int groupId);


    int getGroup(int level, int aisle);
}
