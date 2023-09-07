package com.lee.database.mk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.database.mk.entity.Task;
import com.lee.database.mk.mapper.TaskMapper;
import com.lee.database.mk.service.ITaskService;
import com.lee.util.Constance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lee
 * @since 2022-06-28
 */
@Service
//@Transactional
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements ITaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public List<Task> selectTask(int type, int state, String barcode) {
        return taskMapper.selectAllByStateAndTypeAndBoxNumberAndFinishTimeIsNull(type, state, barcode);
    }

    @Override
    public List<Task> lineTask() {
        return taskMapper.selectAllByStateAndSite(0, "0");
    }

    @Override
    public List<Task> outboundTaskLF(int level, int aisle) {
        return taskMapper.selectList(new QueryWrapper<Task>()
                .eq("type", 2)
                .eq("s_level", level)
                .in("state", 0, 1, 2)
                .like("s_pos", aisle + "%")
                .select("id", "type", "s_level", "s_pos", "state", "box_number"));

    }

    @Override
    public int initTaskIdByType(int type) {
        return taskMapper.selectList(new QueryWrapper<Task>().eq("type", type)).size() + 1;
    }

    @Override
    public int initTaskIdByLift(int liftPos) {
        return taskMapper.selectList(new QueryWrapper<Task>().eq("s_pos", liftPos)).size() + 1;
    }

    @Override
    public List<Task> inboundTask() {
        return taskMapper.selectList(new QueryWrapper<Task>()
                .eq("type", 1)
                .in("state", 0, 1, 2, 7));
    }

    @Override
    public List<Task> inboundTaskByLift(int liftId) {
        return taskMapper.selectList(new QueryWrapper<Task>()
                .eq("type", 1)
                .in("state", 0, 1, 2, 7)
                .like("box_number", Constance.BOX_PREFIX + liftId + "-%")
        );
    }

    @Override
    public List<Task> inboundTaskGroupByLevel(int liftId, String site) {

        return taskMapper.selectList(new LambdaQueryWrapper<Task>()
                .eq(Task::getType, 1)
                .eq(Task::getSite, site)
                .eq(Task::getState, 0)
                .like(Task::getBoxNumber, Constance.BOX_PREFIX + liftId + "-%")
                .groupBy(Task::getSLevel)
                .orderByAsc(Task::getSLevel)
        );
    }

    @Override
    public Task inboundTaskState1(int level, int liftId) {
        String preStr = Constance.BOX_PREFIX + liftId + "-";
        return taskMapper.selectOne(new QueryWrapper<Task>()
                .eq("type", 1)
                .eq("s_level", level)
                .like("box_number", preStr + "%")
                //.eq("s_pos", pos)
                .in("state", 1));
    }

    @Override
    public List<Task> outboundTask(int level) {
        return taskMapper.selectList(new QueryWrapper<Task>()
                .eq("type", 2)
                .eq("s_level", level)
                .in("state", 0, 1, 2).select("id", "type", "s_level", "state", "box_number"));
    }

    @Override
    public List<Task> moveTask(int level) {
        return taskMapper.selectList(new QueryWrapper<Task>()
                .eq("type", 15)
                .eq("s_level", level)
                .in("state", 0, 1, 2, 7));
    }

}
