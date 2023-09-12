package com.lee.database.std.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.database.std.entity.Locations;
import com.lee.database.std.mapper.LocationsStdMapper;
import com.lee.database.std.service.ILocationsService;
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
 * @since 2022-06-24
 */
@Service
@Transactional
public class StdLocationsServiceImpl extends ServiceImpl<LocationsStdMapper, Locations> implements ILocationsService {

    @Autowired
    private LocationsStdMapper locationsMapperStd;

    @Override
    public List<Integer> allLevels() {
        List<Locations> locations = locationsMapperStd.selectList(new QueryWrapper<Locations>()
//                        .select("\"level\"","location","pos","aisle","state","type")
                        .eq("type", 0)
                        .select("DISTINCT \"level\"")
                        .orderByAsc("\"level\"")
        );
        List<Integer> levelChoices = new ArrayList<>();
        locations.forEach(lt -> levelChoices.add(lt.getLevel()));
        return levelChoices;
    }

    @Override
    public List<Integer> allAisles() {
        List<Locations> locations = locationsMapperStd.selectList(new QueryWrapper<Locations>()
                .select("\"level\"", "location", "pos", "aisle", "state", "type")
                .eq("type", 0)
                .select("DISTINCT aisle")
                .orderByAsc("aisle"));
        List<Integer> aisleChoices = new ArrayList<>();
        locations.forEach(lt -> aisleChoices.add(lt.getAisle()));
        return aisleChoices;
    }

    @Override
    public List<Locations> outLocations() {
        return locationsMapperStd.selectList(new QueryWrapper<Locations>()
                .select("\"level\"", "location", "pos", "aisle", "state", "type")
                .eq("type", 0));
    }

    @Override
    public List<Locations> outLocations(int level) {
        return locationsMapperStd.selectList(new QueryWrapper<Locations>()
                .eq("\"level\"", level)
                //.eq("state", 10)
                .eq("type", 0));
    }

    @Override
    public List<Locations> inboundLocations(int level) {
        if (level == -1) {
            return locationsMapperStd.selectList(new QueryWrapper<Locations>()
                    .select("\"level\"", "pos", "location", "aisle", "type", "state", "box_number")
                    .eq("state", 0)
                    .eq("type", 0));
        }
        return locationsMapperStd.selectList(new QueryWrapper<Locations>()
                .select("\"level\"", "pos", "location", "aisle", "type", "state", "box_number")
                .eq("\"level\"", level)
                .eq("state", 0)
                .eq("type", 0));
    }

    @Override
    public List<Locations> outLocations(int level, int aisle) {
        return locationsMapperStd.selectList(new QueryWrapper<Locations>()
                //.select("level")
                .eq("\"level\"", level)
                //.eq("state", 10)
                .eq("aisle", aisle)
                .eq("type", 0));
    }

    @Override
    public List<Locations> inboundLocations(int level, int aisle) {
        return locationsMapperStd.selectList(new QueryWrapper<Locations>()
                .select("\"level\"", "location", "pos", "state", "aisle", "type", "box_number")
                .eq("level", level)
                .eq("state", 0)
                .eq("aisle", aisle)
                .eq("type", 0));
    }

    @Override
    public int occupyLocation(Locations location) {
        return locationsMapperStd.update(null, new UpdateWrapper<Locations>()
                .eq("\"level\"", location.getLevel())
                .eq("location", location.getLocation())
                .eq("state", 10)
                .eq("type", 0)
                .set("state", 1));
    }
}
