package com.lee.database.dss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.database.dss.entity.DeviceBoxLift;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lee
 * @since 2022-08-31
 */
public interface IDeviceBoxLiftService extends IService<DeviceBoxLift> {

    List<DeviceBoxLift> inboundLiftByAisle(List<Integer> aisles);

    List<DeviceBoxLift> outboundLiftByAisle();

}
