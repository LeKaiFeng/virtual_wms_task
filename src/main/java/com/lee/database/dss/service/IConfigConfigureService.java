package com.lee.database.dss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.database.dss.entity.ConfigConfigure;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lee
 * @since 2022-09-06
 */
public interface IConfigConfigureService extends IService<ConfigConfigure> {

    String getConfigValue(String name);
}
