package com.lee.database.std.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.database.std.entity.Inbound;
import com.lee.database.std.mapper.InboundStdMapper;
import com.lee.database.std.service.IInboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lee
 * @since 2022-06-24
 */
@Service
public class StdInboundServiceImpl extends ServiceImpl<InboundStdMapper, Inbound> implements IInboundService {

    @Autowired
    private InboundStdMapper inboundStdMapper;

    @Override
    public List<Inbound> selectInboundState() {
        return inboundStdMapper.selectList(new QueryWrapper<Inbound>().eq("state", 3));
    }
}
