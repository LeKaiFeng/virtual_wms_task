package com.lee.database.mk.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lee.database.mk.entity.Position;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * @author DELL
 * @description 针对表【ga_position】的数据库操作Mapper
 * @createDate 2023-05-24 09:13:00
 * @Entity com.lee.database.mk.entity.Position
 */
@Repository
public interface PositionMapper extends BaseMapper<Position> {

    List<Position> selectAllByLevelAndAisle(@Param("level") Integer level, @Param("aisle") Integer aisle);


}




