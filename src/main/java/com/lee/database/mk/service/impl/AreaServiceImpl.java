package com.lee.database.mk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.database.mk.entity.Area;
import com.lee.database.mk.mapper.AreaMapper;
import com.lee.database.mk.service.IAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lee
 * @since 2022-06-28
 */
@Service
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements IAreaService {

    @Autowired
    private AreaMapper areaMapper;

    @Override
    public List<Area> areas(int level) {
        return areaMapper.selectList(new QueryWrapper<Area>()
                        .eq("level", level)
                        .select("DISTINCT car_group")
                //.groupBy("car_group")
        );
    }

    @Override
    public List<Integer> getAisles(int level, int car_group, boolean isExitArea) {
        List<Area> areas;
        if (isExitArea) {
            areas = areaMapper.selectList(new QueryWrapper<Area>()
                    .eq("level", level).eq("car_group", car_group));
        } else {

            areas = areaMapper.selectList(new QueryWrapper<Area>()
                    .eq("level", level).ne("car_group", car_group));

        }

        List<Integer> list = new ArrayList<>();
        areas.forEach(area -> list.add(area.getAisle()));
        return list;

    }

    @Override
    public int getGroup(int level, int aisle) {
        return areaMapper.selectOne(new QueryWrapper<Area>()
                        .eq("level", level)
                        .eq("aisle", aisle))
                .getCarGroup();
    }
}
