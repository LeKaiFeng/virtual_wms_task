package com.lee.database.mk.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.database.mk.entity.Locations;
import com.lee.database.mk.mapper.LocationsMapper;
import com.lee.database.mk.service.ILocationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lee
 * @since 2022-06-20
 */
@Service
@Transactional
public class LocationsServiceImpl extends ServiceImpl<LocationsMapper, Locations> implements ILocationsService {

    @Autowired
    private LocationsMapper locationsMapper;

    @Override
    public List<Integer> allLevels() {

        List<Locations> locations = locationsMapper.selectList(new QueryWrapper<Locations>()
                .eq("type", 0).select("DISTINCT level").orderByAsc("level"));
        List<Integer> levelChoices = new ArrayList<>();
        locations.forEach(lt -> levelChoices.add(lt.getLevel()));
        return levelChoices;

    }

    @Override
    public List<Integer> allAisles() {

        List<Locations> locations = locationsMapper.selectList(new QueryWrapper<Locations>()
                .eq("type", 0).select("DISTINCT aisle").orderByAsc("aisle"));
        List<Integer> aisleChoices = new ArrayList<>();
        locations.forEach(lt -> aisleChoices.add(lt.getAisle()));
        return aisleChoices;

    }

    @Override
    public List<Locations> allAisle() {
        return locationsMapper.selectList(new QueryWrapper<Locations>().eq("type", 0).select("DISTINCT aisle").orderByAsc("aisle"));
    }

    @Override
    public Locations getLocation(int level, int aisle) {
        List<Locations> locations = locationsMapper.selectList(new QueryWrapper<Locations>()
                        .eq("level", level)
                        .eq("aisle", aisle)
                        .eq("type", 0)
                //.eq("state", 0)
        );
        if (locations.size() == 0) {
            return null;
        }
        return locations.get(RandomUtil.randomInt(locations.size()));
    }

    @Override
    public Locations moveLocation(int level, List<Integer> aisles) {
        if (aisles.size() == 0) {
            return null;
        }
        List<Locations> locations = locationsMapper.selectList(new QueryWrapper<Locations>()
                .eq("level", level)
                .in("aisle", aisles)
                .eq("type", 0));
        if (locations.size() == 0) {
            return null;
        }
        return locations.get(RandomUtil.randomInt(locations.size()));

    }

    @Override
    public List<Locations> outboundLocations(int level) {
        return locationsMapper.selectList(new QueryWrapper<Locations>()
                .eq("level", level)
                //.eq("aisle",aisle)
                //.eq("state",10)
                .eq("type", 0)
        );
    }

    @Override
    public List<Locations> outboundLocations(int level, int aisle) {
        return locationsMapper.selectList(new QueryWrapper<Locations>()
                .eq("level", level)
                .eq("aisle", aisle)
                //.eq("state",10)
                .eq("type", 0)
        );
    }

    @Override
    public List<Locations> inboundLocations(int level, int liftPos) {
        if (liftPos == 810100) {
            return locationsMapper.selectList(new QueryWrapper<Locations>()
                    .eq("level", level)
                    .le("aisle", 81)
                    //.eq("state",0)
                    .eq("type", 0)
            );
        }
        return locationsMapper.selectList(new QueryWrapper<Locations>()
                .eq("level", level)
                .ge("aisle", 91)
                //.eq("state",0)
                .eq("type", 0)
        );
    }


    @Override
    public List<Locations> inbound(int level, int aisle) {
        return locationsMapper.selectList(new QueryWrapper<Locations>()
                .eq("level", level)
                .ge("aisle", aisle)
                .eq("state", 0)
                .eq("type", 0)
        );
    }

    @Override
    public List<Locations> inbound(int level) {
        return locationsMapper.selectList(new QueryWrapper<Locations>()
                .eq("level", level)
                //.eq("state",0)
                .eq("type", 0)
        );
    }

    @Override
    public int occupy(Locations location) {
        return locationsMapper.update(null, new UpdateWrapper<Locations>()
                .eq("level", location.getLevel())
                .eq("location", location.getLocation())
                .eq("type", 0)
                .set("state", 1)
                .set("box_number", location.getBoxNumber())
        );
    }
}
