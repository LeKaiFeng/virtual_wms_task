package com.lee.database.std.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.database.std.entity.Boxliftshelfpd;
import com.lee.database.std.mapper.BoxliftshelfpdStdMapper;
import com.lee.database.std.service.IBoxliftshelfpdService;
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
public class StdBoxliftshelfpdServiceImpl extends ServiceImpl<BoxliftshelfpdStdMapper, Boxliftshelfpd> implements IBoxliftshelfpdService {

    @Autowired
    private BoxliftshelfpdStdMapper boxliftshelfpdStdMapper;

    @Override
    public List<Boxliftshelfpd> selectPds(int pos) {
        return boxliftshelfpdStdMapper.selectList(new QueryWrapper<Boxliftshelfpd>()
                .select("id", "pos", "\"level\"", "inbound_state", "inbound_request", "inbound_box", "ip", "port", "type")
                .eq("pos", pos)
        );
    }

    @Override
    public Boxliftshelfpd selectPds(int level, int pos) {
        return boxliftshelfpdStdMapper.selectOne(new QueryWrapper<Boxliftshelfpd>()
                .eq("level", level)
                .eq("pos", pos));
    }

    @Override
    public List<Boxliftshelfpd> selectPds(int level, int pos, int dir) {
        return boxliftshelfpdStdMapper.selectList(new QueryWrapper<Boxliftshelfpd>()
                .select("id", "level", "pos", "pd_type", "inbound_request")
                .eq("level", level)
                .eq("pos", pos)
                .like("inbound_location", "%" + dir)
                .eq("pd_type", 1));
    }

    @Override
    public List<Boxliftshelfpd> pdRequest(int pos) {
        return boxliftshelfpdStdMapper.selectList(new QueryWrapper<Boxliftshelfpd>()
                .eq("pos", pos)
                .eq("inbound_request", 1));
    }


}
