package com.lee.database.mk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lee.database.mk.entity.Locations;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lee
 * @since 2022-06-20
 */
public interface ILocationsService extends IService<Locations> {


    List<Integer> allLevels();

    List<Locations> allAisle();

    List<Integer> allAisles();

    Locations getLocation(int level, int aisle);

    Locations moveLocation(int level, List<Integer> aisles);

    List<Locations> outboundLocations(int level);

    List<Locations> outboundLocations(int level, int aisle);

    List<Locations> inboundLocations(int level, int aisle);

    public List<Locations> inbound(int level, int aisle);

    public List<Locations> inbound(int level);

    public int occupy(Locations locations);
}
