package com.lee.database.dss.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.database.dss.entity.ResourceTask;
import com.lee.database.dss.mapper.ResourceTaskMapper;
import com.lee.database.dss.service.IResourceTaskService;
import com.lee.database.mk.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class ResourceTaskServiceImpl extends ServiceImpl<ResourceTaskMapper, ResourceTask> implements IResourceTaskService {

    @Autowired
    private ResourceTaskMapper taskMapper;


    public int initTaskIdByType(int type) {
        return taskMapper.selectList(new QueryWrapper<ResourceTask>().eq("type", type)).size() + 1;
    }

    @Override
    public int initBoxId(int boxLiftPos) {
        ResourceTask task = taskMapper.selectOne(new QueryWrapper<ResourceTask>()
                .eq("type", 1)
                .eq("start_pos", boxLiftPos)
                .orderByDesc("id")
                .last("limit 1"));
        if (task == null) {
            return 1;
        }
        if (!task.getBarcode().contains("-")) {
            return 1;
        }
        List<String> split = StrUtil.split(task.getBarcode(), "-");
        return Convert.toInt(split.get(2)) + 1;
    }

    @Override
    public List<ResourceTask> searchOutboundTask(int level, List<Integer> aisle) {
        if (level == -1 && aisle == null) {
            return taskMapper.selectList(new QueryWrapper<ResourceTask>()
                    .select("id,start_level,state,type,barcode")
                    .eq("type", 2)
                    //.eq("start_aisle", aisle)
                    .in("state", 0, 1, 2));
        }
        if (aisle == null) {
            return taskMapper.selectList(new QueryWrapper<ResourceTask>()
                    .select("id,start_level,state,type,barcode")
                    .eq("start_level", level)
                    .eq("type", 2)
                    //.eq("start_aisle", aisle)
                    .in("state", 0, 1, 2));
        }
        return taskMapper.selectList(new QueryWrapper<ResourceTask>()
                .select("id,start_level,state,type,barcode")
                .eq("start_level", level)
                .eq("type", 2)
                .in("start_aisle", aisle)
                .in("state", 0, 1, 2));
    }

    @Override
    public List<ResourceTask> searchInboundTask(int level, List<Integer> aisles) {
        return taskMapper.selectList(new QueryWrapper<ResourceTask>()
                .select("id,start_level,state,type,barcode")
                .eq("start_level", level)
                .eq("type", 1)
                .in("start_aisle", aisles)
                .in("state", 0, 1, 2, 7));
    }

    @Override
    public List<ResourceTask> getLiftInboundTask(int level, int liftId) {
        if (level == -1) {
            return taskMapper.selectList(new QueryWrapper<ResourceTask>()
                    .select("id,start_level,state,type,barcode")
                    .eq("type", 1)
                    .like("barcode", "BoxLift-" + liftId + "-%")
                    .in("state", 0, 1, 2, 7));
        }
        return taskMapper.selectList(new QueryWrapper<ResourceTask>()
                .select("id,start_level,state,type,barcode")
                .eq("type", 1)
                .eq("start_level", level)
                .like("barcode", "BoxLift-" + liftId + "-%")
                .in("state", 0, 1, 2, 7));
    }

    @Override
    public List<ResourceTask> searchMoveTask(int level, List<Integer> aisles) {
        if (aisles == null) {
            return taskMapper.selectList(new QueryWrapper<ResourceTask>()
                    .select("id,start_level,state,type,barcode")
                    .eq("type", 15)
                    .eq("start_level", level)
                    .in("state", 0, 1, 2, 7));
        }
        return taskMapper.selectList(new QueryWrapper<ResourceTask>()
                .select("id,start_level,state,type,barcode")
                .eq("type", 15)
                .eq("start_level", level)
                .in("start_aisle", aisles)
                .in("state", 0, 1, 2, 7));
    }

    @Override
    public ResourceTask isOnLiftTask(String barcode, int level) {
        return taskMapper.selectOne(new QueryWrapper<ResourceTask>()
                .select("id,start_level,state,type,barcode")
                .eq("type", 1)
                .eq("start_level", level)
                .eq("barcode", barcode)
                .in("state", 1));
    }

    @Override
    public ResourceTask isExitTask(int level, String boxId) {
        return taskMapper.selectOne(new QueryWrapper<ResourceTask>()
                .select("id,start_level,state,type,barcode")
                .eq("start_level", level)
                .eq("type", 1)
                .eq("barcode", boxId)
                .in("state", 0, 1, 2, 7));
    }
}
