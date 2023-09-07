package com.lee.database.mk.service;

import com.baomidou.mybatisplus.extension.service.IService;
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
public interface IVirtualMessageService extends IService<VirtualMessage> {

    int updateMegFinish(String msg);


    /**
     * mfc client {id，msg}
     *
     * @return
     */

    //查找所有状态为0的，这条类型的消息
    VirtualMessage selectMsg(String request);

    int initMegId();

    List<VirtualMessage> requestLocations();
}
