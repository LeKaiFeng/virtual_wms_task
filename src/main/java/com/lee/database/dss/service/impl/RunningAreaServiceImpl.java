package com.lee.database.dss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.database.dss.entity.RunningArea;
import com.lee.database.dss.mapper.RunningAreaMapper;
import com.lee.database.dss.service.IRunningAreaService;
import com.lee.database.mk.entity.Area;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lee
 * @since 2022-09-01
 */
@Service
public class RunningAreaServiceImpl extends ServiceImpl<RunningAreaMapper, RunningArea> implements IRunningAreaService {


    @Autowired
    private RunningAreaMapper areaMapper;

    @Override
    public List<RunningArea> groupSize(int level) {
        return areaMapper.selectList(new QueryWrapper<RunningArea>()
                .eq("level", level).groupBy("car_group"));
    }

    @Override
    public List<RunningArea> sameGroupAisle(int level, int groupId) {
        return areaMapper.selectList(new QueryWrapper<RunningArea>()
                .select("level", "aisle", "car_group")
                .eq("level", level)
                .eq("car_group", groupId));
    }

    @Override
    public int getGroup(int level, int aisle) {
        return areaMapper.selectOne(new QueryWrapper<RunningArea>()
                        .eq("level", level)
                        .eq("aisle", aisle))
                .getCarGroup();
    }


}
