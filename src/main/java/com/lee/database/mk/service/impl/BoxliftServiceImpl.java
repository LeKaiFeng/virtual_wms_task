package com.lee.database.mk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.database.mk.entity.Boxlift;
import com.lee.database.mk.mapper.BoxliftMapper;
import com.lee.database.mk.service.IBoxliftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lee
 * @since 2022-06-20
 */
@Service
@Transactional
public class BoxliftServiceImpl extends ServiceImpl<BoxliftMapper, Boxlift> implements IBoxliftService {

    @Autowired
    private BoxliftMapper boxliftMapper;

    @Override
    public List<Boxlift> allBoxLifts() {
        return boxliftMapper.selectList(new LambdaQueryWrapper<Boxlift>().eq(Boxlift::getIsMaster, 1));
    }


    @Override
    public List<Boxlift> inboundLift() {
        return boxliftMapper.selectList(new QueryWrapper<Boxlift>().eq("switch_model", 1));
    }

    @Override
    public List<Boxlift> outboundLift() {
        return boxliftMapper.selectList(new QueryWrapper<Boxlift>().eq("switch_model", 2));
    }

    @Override
    public String getIp() {
        return boxliftMapper.selectOne(new QueryWrapper<Boxlift>().last("limit 1").select("ip")).getIp();
    }

    @Override
    public Boxlift getInboundLift(int pos) {
        return boxliftMapper.selectOne(new QueryWrapper<Boxlift>().eq("switch_model", 1).eq("pos", pos));
    }
}
