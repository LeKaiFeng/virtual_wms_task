package com.lee.database.std.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.database.std.entity.Boxliftshelfpd;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lee
 * @since 2022-06-24
 */
public interface IBoxliftshelfpdService extends IService<Boxliftshelfpd> {
    List<Boxliftshelfpd> selectPds(int pos);

    List<Boxliftshelfpd> selectPds(List<Integer> pdPos);

    Boxliftshelfpd selectPds(int level, int pos);

    List<Boxliftshelfpd> selectPds(int level, int pos, int dir);

    List<Boxliftshelfpd> pdRequest(int pos);

}
