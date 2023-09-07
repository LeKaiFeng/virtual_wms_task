package com.lee.database.std.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.database.std.entity.Inbound;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lee
 * @since 2022-06-24
 */
public interface IInboundService extends IService<Inbound> {


    List<Inbound> selectInboundState();
}
