package com.lee.database.mk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.database.mk.entity.Appointannounce;
import com.lee.database.mk.entity.Task;
import com.lee.database.mk.mapper.AppointannounceMapper;
import com.lee.database.mk.service.IAppointannounceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lee
 * @since 2022-06-20
 */
@Service
public class AppointannounceServiceImpl extends ServiceImpl<AppointannounceMapper, Appointannounce> implements IAppointannounceService {

    @Autowired
    protected AppointannounceMapper appointannounceMapper;

    @Override
    public int initMsgId() {
        Appointannounce appoint = appointannounceMapper.selectOne(new QueryWrapper<Appointannounce>()
                .orderByDesc("id")
                .last("limit 1")
                .select("id"));
        if (appoint == null) {
            return 1;
        }
        return appoint.getId() + 1;
    }
}
