package com.lee.database.std.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.database.std.entity.Announce;
import com.lee.database.std.entity.Appointannounce;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lee
 * @since 2022-06-24
 */
public interface IAnnounceService extends IService<Announce> {

    List<Announce> selectByIdAndLevel(int liftId, int level);

    List<Announce> selectAnnounce(String area);

    List<Announce> selectAnnounce();

    int initId();

    List<Announce> getFeiCeAnnounce();

    List<Announce> isExitAnnounce(int level, int liftId);

    List<Announce> checkAnnounce(String pre);

    Announce isExitAnnounce(String boxId);

    List<Announce> isExitAnnounce(int liftId);

    List<Announce> isExitAnnounceSK(int level, String boxId);
}
