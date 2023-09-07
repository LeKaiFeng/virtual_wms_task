package com.lee.database.std.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.database.std.entity.Locations;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lee
 * @since 2022-06-24
 */
public interface ILocationsService extends IService<Locations> {

    List<Integer> allLevels();

    List<Integer> allAisles();

    List<Locations> outLocations();

    List<Locations> outLocations(int level);

    List<Locations> inboundLocations(int level);

    List<Locations> outLocations(int level, int aisle);

    List<Locations> inboundLocations(int level, int aisle);

    int occupyLocation(Locations location);

}
