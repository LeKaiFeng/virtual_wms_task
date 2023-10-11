package com.lee.database.std.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.database.std.entity.Announce;
import com.lee.database.std.entity.Appointannounce;
import com.lee.database.std.mapper.AnnounceStdMapper;
import com.lee.database.std.service.IAnnounceService;
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
public class StdAnnounceServiceImpl extends ServiceImpl<AnnounceStdMapper, Announce> implements IAnnounceService {


    @Autowired
    protected AnnounceStdMapper announceStdMapper;


    @Override
    public List<Announce> selectByIdAndLevel(int liftId, int level) {
        return announceStdMapper.selectList(new QueryWrapper<Announce>()
                .eq("state", 0)
                .eq("level", level)
                .like("box_number", Constance.BOX_PREFIX + liftId + "%"));
    }

    @Override
    public List<Announce> selectAnnounce(String area) {
        return announceStdMapper.selectList(new QueryWrapper<Announce>()
                .eq("state", 0)
                .eq("area", area)
                .isNull("arrive_time"));
    }

    @Override
    public List<Announce> selectAnnounce() {

        return announceStdMapper.selectList(new QueryWrapper<Announce>()
                .eq("state", 0)
                .isNull("arrive_time"));
    }

    @Override
    public int initId() {
        //获取最后一条消息的message 的 bodyId
        Announce announce = announceStdMapper.selectOne(new QueryWrapper<Announce>()
                .orderByDesc("id").last("limit 1"));
        if (announce == null) {
            return 0;
        }
        return announce.getId();
    }

    @Override
    public List<Announce> getFeiCeAnnounce() {
        return announceStdMapper.selectList(new QueryWrapper<Announce>()
                .eq("state", 0)
                .ne("area", "B")
                .isNull("arrive_time")
                .orderByAsc("announce_time")
                .last("limit 2"));
    }

    @Override
    public List<Announce> isExitAnnounce(int level, int liftId) {
        return announceStdMapper.selectList(new QueryWrapper<Announce>()
                .eq("state", 0)
                .eq("level", level)
                .like("box_number", Constance.BOX_PREFIX + liftId + "%")
        );
    }

    @Override
    public List<Announce> checkAnnounce(String pre) {
        return announceStdMapper.selectList(new QueryWrapper<Announce>()
                .eq("state", 0)
                .like("box_number", Constance.LIFT_PREFIX + "%")
        );
    }

    @Override
    public Announce isExitAnnounce(String boxId) {
        return announceStdMapper.selectOne(new QueryWrapper<Announce>()
                .select("id", "level", "box_number", "state")
                .eq("state", 0)
                .eq("box_number", boxId)
        );
    }

    @Override
    public List<Announce> isExitAnnounceSK(int level, String boxId) {
        return announceStdMapper.selectList(new QueryWrapper<Announce>()
                .eq("level", level)
                .eq("box_number", boxId)
        );
    }

    @Override
    public List<Announce> isExitAnnounce(int liftId) {
        return announceStdMapper.selectList(new QueryWrapper<Announce>()
                .select("id", "box_number", "wmsid", "state", "level", "location")
                .eq("state", 0)
                .like("box_number", Constance.BOX_PREFIX + liftId + "-%")
        );
    }
}
