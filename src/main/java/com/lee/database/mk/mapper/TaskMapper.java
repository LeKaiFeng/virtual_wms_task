package com.lee.database.mk.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lee.database.mk.entity.Task;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * @author DELL
 * @description 针对表【ga_task】的数据库操作Mapper
 * @createDate 2023-05-24 14:20:42
 * @Entity com.lee.database.mk.entity.Task
 */
@Repository
public interface TaskMapper extends BaseMapper<Task> {

    List<Task> selectAllByStateAndTypeOrderByELevel(@Param("state") Integer state, @Param("type") Integer type);

    List<Task> selectAllByStateAndSite(@Param("state") Integer state, @Param("site") String site);

    List<Task> selectByStateAndTypeAndBoxNumberAndSLevelAndFinishTimeIsNull(@Param("state") Integer state, @Param("type") Integer type,
                                                                            @Param("boxNumber") String boxNumber, @Param("sLevel") Integer sLevel);

    List<Task> selectAllByStateAndTypeAndBoxNumberAndFinishTimeIsNull(@Param("state") Integer state, @Param("type") Integer type, @Param("boxNumber") String boxNumber);
}




