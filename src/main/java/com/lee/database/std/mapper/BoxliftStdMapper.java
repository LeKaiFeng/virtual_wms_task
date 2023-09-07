package com.lee.database.std.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

import com.lee.database.std.entity.Boxlift;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * @author DELL
 * @description 针对表【ga_boxlift】的数据库操作Mapper
 * @createDate 2023-06-04 20:49:57
 * @Entity com.lee.database.std.entity.Boxlift
 */
@Repository
public interface BoxliftStdMapper extends BaseMapper<Boxlift> {

    List<Boxlift> selectAll();

    List<Boxlift> selectAllById(@Param("id") Integer id);

    int updateVirtualLevelById(@Param("virtualLevel") Integer virtualLevel, @Param("id") Integer id);

}




