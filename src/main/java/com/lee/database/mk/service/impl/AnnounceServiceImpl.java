package com.lee.database.mk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.database.mk.entity.Announce;
import com.lee.database.mk.mapper.AnnounceMapper;
import com.lee.database.mk.service.IAnnounceService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lee
 * @since 2022-06-20
 */
@Service
public class AnnounceServiceImpl extends ServiceImpl<AnnounceMapper, Announce> implements IAnnounceService {

}
