package com.lee.database.mk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.database.mk.entity.Boxliftshelfpd;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lee
 * @since 2022-06-28
 */
public interface IBoxliftshelfpdService extends IService<Boxliftshelfpd> {


    List<Boxliftshelfpd> getPd(int level, int pos, int mode, int leftRight);

    List<Boxliftshelfpd> getPds(int pos);

    Boxliftshelfpd getPd(int level, int pos);


    List<Integer> getPdLevel();
}
