package com.lee.database.mk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.database.mk.entity.Boxlift;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lee
 * @since 2022-06-20
 */
public interface IBoxliftService extends IService<Boxlift> {


    List<Boxlift> allBoxLifts();

    List<Boxlift> inboundLift();

    List<Boxlift> outboundLift();

    String getIp();

    Boxlift getInboundLift(int pos);
}
