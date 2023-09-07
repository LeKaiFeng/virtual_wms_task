package com.lee.database.std.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.database.std.entity.Boxlift;
import com.lee.database.std.mapper.BoxliftStdMapper;
import com.lee.database.std.service.IBoxliftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lee
 * @since 2022-07-06
 */
@Service
public class StdBoxliftServiceImpl extends ServiceImpl<BoxliftStdMapper, Boxlift> implements IBoxliftService {

    @Autowired
    private BoxliftStdMapper stdBoxLiftMapper;

    @Override
    public Boxlift selectById(int id) {
        return stdBoxLiftMapper.selectById(id);
    }

    @Override
    public int cleanVirtualLevel(int liftId) {

        return stdBoxLiftMapper.update(null, new UpdateWrapper<Boxlift>().eq("id", liftId).set("virtual_level", 0));
    }
}
