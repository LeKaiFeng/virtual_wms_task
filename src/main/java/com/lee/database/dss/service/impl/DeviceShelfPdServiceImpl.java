package com.lee.database.dss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.database.dss.entity.DeviceShelfPd;
import com.lee.database.dss.mapper.DeviceShelfPdMapper;
import com.lee.database.dss.service.IDeviceShelfPdService;
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
public class DeviceShelfPdServiceImpl extends ServiceImpl<DeviceShelfPdMapper, DeviceShelfPd> implements IDeviceShelfPdService {

    @Autowired
    private DeviceShelfPdMapper shelfPdMapper;


    @Override
    public DeviceShelfPd selectPd(int level, int inboundPos) {
        return shelfPdMapper.selectOne(new QueryWrapper<DeviceShelfPd>()
                .eq("level", level).eq("inbound_pos", inboundPos).eq("active", 1));

    }

    @Override
    public List<DeviceShelfPd> selectPds(int inboundPos) {
        return shelfPdMapper.selectList(new QueryWrapper<DeviceShelfPd>().eq("inbound_pos", inboundPos).eq("active", 1));

    }
}
