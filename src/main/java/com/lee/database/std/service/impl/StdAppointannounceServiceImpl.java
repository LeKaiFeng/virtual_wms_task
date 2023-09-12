package com.lee.database.std.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.database.std.entity.Appointannounce;
import com.lee.database.std.entity.Boxlift;
import com.lee.database.std.mapper.AppointannounceStdMapper;
import com.lee.database.std.service.IAppointannounceService;
import com.lee.util.Constance;
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
public class StdAppointannounceServiceImpl extends ServiceImpl<AppointannounceStdMapper, Appointannounce> implements IAppointannounceService {

    @Autowired
    private AppointannounceStdMapper appointannounceStdMapper;

    @Override
    public List<Appointannounce> selectAllById(int liftId) {

        return appointannounceStdMapper.selectList(new QueryWrapper<Appointannounce>()
                .eq("state", 0).like("box_number", Constance.BOX_PREFIX + liftId + "%"));

    }

    @Override
    public List<Appointannounce> selectByIdAndLevel(int liftId, int level) {
        return appointannounceStdMapper.selectList(new QueryWrapper<Appointannounce>()
                .eq("state", 0).eq("level", level).like("box_number", Constance.BOX_PREFIX + liftId + "%"));
    }

    @Override
    public List<Appointannounce> isExitAppoint(int level, int liftId) {
        if (level == -1) {
            return appointannounceStdMapper.selectList(new QueryWrapper<Appointannounce>()
                    .select("id", "\"level\"", "box_number", "state")
                    .eq("state", 0)
                    .like("box_number", Constance.BOX_PREFIX + liftId + "-%")
            );
        }
        return appointannounceStdMapper.selectList(new QueryWrapper<Appointannounce>()
                .select("id", "\"level\"", "box_number", "state")
                .eq("state", 0)
                .eq("\"level\"", level)
                .like("box_number", Constance.BOX_PREFIX + liftId + "-%")
        );
    }


    @Override
    public List<Appointannounce> isExitAppoint(String barcode) {
        return appointannounceStdMapper.selectList(new QueryWrapper<Appointannounce>()
                .eq("state", 0)
                .eq("box_number", barcode)
        );
    }

    @Override
    public int initAppointIdSK(Boxlift lift) {
        Appointannounce task = appointannounceStdMapper.selectOne(new QueryWrapper<Appointannounce>()
                //.like("pos", lift.getPos()/1000+"%")
                .select("box_number", "id")
                .like("box_number", lift.getId() + "-%")
                .orderByDesc("id")
                .last("limit 1"));
        if (task == null) {
            return 1;
        }
        List<String> split = StrUtil.split(task.getBoxNumber(), "-");
        return Convert.toInt(split.get(1)) + 1;
    }

    @Override
    public int initAppointId(Boxlift lift) {
        Appointannounce task = appointannounceStdMapper.selectOne(new QueryWrapper<Appointannounce>()
                //.like("pos", lift.getPos()/1000+"%")
                .select("box_number", "id")
                .like("box_number", Constance.BOX_PREFIX + lift.getId() + "-%")
                .orderByDesc("id")
                .last("limit 1"));
        if (task == null) {
            return 1;
        }
        List<String> split = StrUtil.split(task.getBoxNumber(), "-");
        return Convert.toInt(split.get(2)) + 1;
    }

    @Override
    public int updateState(String boxId) {
        return appointannounceStdMapper.update(null, new UpdateWrapper<Appointannounce>()
                .eq("box_number", boxId)
                .eq("state", 1)
                .set("state", 0));
    }
}
