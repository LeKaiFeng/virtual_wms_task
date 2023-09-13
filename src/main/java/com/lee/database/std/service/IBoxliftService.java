package com.lee.database.std.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.database.std.entity.Boxlift;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lee
 * @since 2022-07-06
 */
public interface IBoxliftService extends IService<Boxlift> {

    Boxlift selectById(int id);

    int cleanVirtualLevel(int liftId);

    List<Boxlift> selectAll();


}
