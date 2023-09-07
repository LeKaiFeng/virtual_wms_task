package com.lee.database.mk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.database.mk.entity.MessageReceive;
import com.lee.database.mk.entity.VirtualMessage;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lee
 * @since 2022-06-20
 */
public interface IMessageReceiveService extends IService<MessageReceive> {

    int initMegId();

}
