package com.lee.database.dss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.database.dss.entity.ResourceLocation;
import com.lee.database.dss.mapper.ResourceLocationMapper;
import com.lee.database.dss.service.IResourceLocationService;
import com.lee.database.mk.entity.Locations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lee
 * @since 2022-08-31
 */
@Service
public class ResourceLocationServiceImpl extends ServiceImpl<ResourceLocationMapper, ResourceLocation> implements IResourceLocationService {

    @Autowired
    private ResourceLocationMapper locationMapper;

    @Override
    public List<Integer> levels() {
        List<ResourceLocation> locations = locationMapper.selectList(new QueryWrapper<ResourceLocation>()
                .eq("type", 0)
                .select("DISTINCT level")
                .orderByAsc("level"));
//        List<ResourceLocation> locations = locationsMapper.selectList(new QueryWrapper<ResourceLocation>()
//                .eq("type", 0).select("DISTINCT level").orderByAsc("level"));
        List<Integer> levelChoices = new ArrayList<>();
        locations.forEach(lt -> levelChoices.add(lt.getLevel()));
        return levelChoices;
//        return locationMapper.selectList(new QueryWrapper<ResourceLocation>().select("DISTINCT level"));
    }

    @Override
    public List<Integer> aisles() {

        List<ResourceLocation> locations = locationMapper.selectList(new QueryWrapper<ResourceLocation>()
                .eq("type", 0).select("DISTINCT aisle").orderByAsc("aisle"));
        List<Integer> aisleChoices = new ArrayList<>();
        locations.forEach(lt -> aisleChoices.add(lt.getAisle()));
        return aisleChoices;

    }

    @Override
    public List<ResourceLocation> outLocations(int level, List<Integer> aisles) {
        if (aisles == null) {
            return locationMapper.selectList(new QueryWrapper<ResourceLocation>()
                    .select("level,location,barcode,aisle")
                    .eq("level", level)
                    .eq("type", 0)
            );
        }
        return locationMapper.selectList(new QueryWrapper<ResourceLocation>()
                .select("level,location,barcode,aisle")
                .in("aisle", aisles)
                .eq("level", level)
                .eq("type", 0)
        );
    }

    @Override
    public List<ResourceLocation> inboundLocations(int level, List<Integer> aisles) {
        if (aisles == null) {
            return locationMapper.selectList(new QueryWrapper<ResourceLocation>()
                    .ne("state", -100)
                    .eq("level", level)
                    .eq("state", 0)
                    .eq("type", 0)
            );
        }
        return locationMapper.selectList(new QueryWrapper<ResourceLocation>()
                .ne("state", -100)
                .in("aisle", aisles)
                .eq("level", level)
                .eq("state", 0)
                .eq("type", 0)
        );
    }

    @Override
    public List<ResourceLocation> aisles(int level) {
        return locationMapper.selectList(new QueryWrapper<ResourceLocation>().select("DISTINCT level,aisle").eq("level", level));
    }
}
