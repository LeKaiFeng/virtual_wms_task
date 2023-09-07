package com.lee.database.mk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.database.mk.entity.Appointannounce;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lee
 * @since 2022-06-20
 */
public interface IAppointannounceService extends IService<Appointannounce> {
    int initMsgId();
}
