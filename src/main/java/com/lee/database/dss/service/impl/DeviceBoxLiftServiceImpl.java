package com.lee.database.dss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.database.dss.entity.DeviceBoxLift;
import com.lee.database.dss.mapper.DeviceBoxLiftMapper;
import com.lee.database.dss.service.IDeviceBoxLiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lee
 * @since 2022-08-31
 */
@Service
public class DeviceBoxLiftServiceImpl extends ServiceImpl<DeviceBoxLiftMapper, DeviceBoxLift> implements IDeviceBoxLiftService {

    @Autowired
    private DeviceBoxLiftMapper boxLiftMapper;


    @Override
    public List<DeviceBoxLift> allLift() {
        return boxLiftMapper.selectList(new QueryWrapper<DeviceBoxLift>().eq("active", 1));
    }

    @Override
    public List<DeviceBoxLift> inboundLiftByAisle(List<Integer> aisles) {
        return boxLiftMapper.selectList(new QueryWrapper<DeviceBoxLift>().select("id,inbound_aisle,inbound_pos").in("inbound_aisle", aisles).eq("active", 1));
    }

    @Override
    public List<DeviceBoxLift> outboundLiftByAisle() {
        return boxLiftMapper.selectList(new QueryWrapper<DeviceBoxLift>().select("DISTINCT outbound_aisle").eq("active", 1));
    }
}
