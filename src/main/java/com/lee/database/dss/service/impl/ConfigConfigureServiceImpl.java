package com.lee.database.dss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.database.dss.entity.ConfigConfigure;
import com.lee.database.dss.mapper.ConfigConfigureMapper;
import com.lee.database.dss.service.IConfigConfigureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lee
 * @since 2022-09-06
 */
@Service
public class ConfigConfigureServiceImpl extends ServiceImpl<ConfigConfigureMapper, ConfigConfigure> implements IConfigConfigureService {

    @Autowired
    private ConfigConfigureMapper configureMapper;

    @Override
    public String getConfigValue(String name) {
        return configureMapper.selectOne(new QueryWrapper<ConfigConfigure>().eq("name", name)).getValue();
    }
}
