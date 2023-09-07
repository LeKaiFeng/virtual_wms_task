package com.lee.database.dss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.database.dss.entity.ResourceLocation;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lee
 * @since 2022-08-31
 */
public interface IResourceLocationService extends IService<ResourceLocation> {
    List<Integer> aisles();

    List<Integer> levels();

    List<ResourceLocation> outLocations(int level, List<Integer> aisles);

    List<ResourceLocation> inboundLocations(int level, List<Integer> aisles);

    List<ResourceLocation> aisles(int level);
}
