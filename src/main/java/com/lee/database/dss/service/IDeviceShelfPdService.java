package com.lee.database.dss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.database.dss.entity.DeviceShelfPd;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lee
 * @since 2022-08-31
 */
public interface IDeviceShelfPdService extends IService<DeviceShelfPd> {
    DeviceShelfPd selectPd(int level, int inboundPos);

    List<DeviceShelfPd> selectPds(int inboundPos);
}
