package com.lee.database.std.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.database.std.entity.Appointannounce;
import com.lee.database.std.entity.Boxlift;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lee
 * @since 2022-06-24
 */
public interface IAppointannounceService extends IService<Appointannounce> {


    List<Appointannounce> selectAllById(int liftId);

    List<Appointannounce> selectByIdAndLevel(int liftId, int level);

    List<Appointannounce> isExitAppoint(int level, int liftId);

    List<Appointannounce> isExitAppoint(String barcode);

    int initAppointIdSK(Boxlift lift);

    int initAppointId(Boxlift lift);

    int updateState(String boxId);

}
