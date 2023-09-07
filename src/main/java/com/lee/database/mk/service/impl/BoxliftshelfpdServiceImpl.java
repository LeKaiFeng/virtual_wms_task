package com.lee.database.mk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.database.mk.entity.Boxliftshelfpd;
import com.lee.database.mk.mapper.BoxliftshelfpdMapper;
import com.lee.database.mk.service.IBoxliftshelfpdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lee
 * @since 2022-06-28
 */
@Service
public class BoxliftshelfpdServiceImpl extends ServiceImpl<BoxliftshelfpdMapper, Boxliftshelfpd> implements IBoxliftshelfpdService {


    @Autowired
    private BoxliftshelfpdMapper boxliftshelfpdMapper;

    @Override
    public List<Boxliftshelfpd> getPd(int level, int pos, int mode, int leftRight) {
        return boxliftshelfpdMapper.selectList(new QueryWrapper<Boxliftshelfpd>()
                .eq("level", level).eq("pos", pos).eq("switch_model", mode).eq("left_right", leftRight));
    }

    @Override
    public List<Boxliftshelfpd> getPds(int pos) {
        return boxliftshelfpdMapper.selectList(new LambdaQueryWrapper<Boxliftshelfpd>().eq(Boxliftshelfpd::getPos, pos));
    }

    @Override
    public Boxliftshelfpd getPd(int level, int pos) {
        return boxliftshelfpdMapper.selectOne(new QueryWrapper<Boxliftshelfpd>()
                .eq("level", level).eq("pos", pos).select("id", "pos", "inbound_state", "inbound_request", "level", "inbound_box"));
    }

    @Override
    public List<Integer> getPdLevel() {
        return null;
    }
}
