package com.lee.database.std.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lee.database.std.entity.Task;
import com.lee.database.std.mapper.TaskStdMapper;
import com.lee.database.std.service.ITaskService;
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
 * @since 2022-06-24
 */
@Service
public class StdTaskServiceImpl extends ServiceImpl<TaskStdMapper, Task> implements ITaskService {


    @Autowired
    private TaskStdMapper stdMapper;

    @Override
    public List<Task> outTask() {
        return stdMapper.selectList(new QueryWrapper<Task>().eq("type", 2).in("status", 0, 1, 2));
    }

    @Override
    public List<Task> outTask(int level) {
        return stdMapper.selectList(new QueryWrapper<Task>()
                .eq("type", 2)
                .eq("s_level", level)
                .in("status", 0, 1, 2));
    }

    @Override
    public int initBoxId(int boxLiftPos) {
        Task task = stdMapper.selectOne(new QueryWrapper<Task>()
                .eq("type", 1)
                .eq("s_pos", boxLiftPos)
                .orderByDesc("id")
                .last("limit 1"));
        if (task == null) {
            return 1;
        }
        List<String> split = StrUtil.split(task.getBoxNumber(), "-");
        return Convert.toInt(split.get(1)) + 1;
    }

    @Override
    public int initId(int type) {
        List<Task> taskList = stdMapper.selectList(new QueryWrapper<Task>().select("type", "id").eq("type", type));
        return taskList.size() + 1;
    }

    @Override
    public int initBoxIdSK(int boxLiftPos) {
        Task task = stdMapper.selectOne(new QueryWrapper<Task>()
                .eq("type", 1)
                .eq("s_pos", boxLiftPos)
                .orderByDesc("id")
                .last("limit 1"));
        if (task == null) {
            return 1;
        }
        List<String> split = StrUtil.split(task.getBoxNumber(), "-");
        return Convert.toInt(split.get(2)) + 1;
    }


    @Override
    public List<Task> outboundTask(int liftPos) {
        return stdMapper.selectList(new QueryWrapper<Task>()
                //.eq("s_level", level)
                .eq("type", 2)
                .like("s_pos", liftPos / 1000 + "%")
                .in("status", 0, 1, 2));
    }

    @Override
    public List<Task> outboundTask(int level, int aisle) {
        return stdMapper.selectList(new QueryWrapper<Task>()
                .eq("type", 2)
                .eq("s_level", level)
                .likeRight("s_pos", (aisle / 10) + "%")
                .in("status", 0, 1, 2)
        );
    }

    @Override
    public List<Task> moveTask(int liftPos) {
        return stdMapper.selectList(new QueryWrapper<Task>()
                //.eq("s_level", level)
                .eq("type", 15)
                .like("s_pos", liftPos / 1000 + "%")
                .in("status", 0, 1, 2));
    }

    @Override
    public List<Task> moveLibrary(int level) {
        return stdMapper.selectList(new QueryWrapper<Task>()
                .eq("type", 15)
                .eq("s_level", level)
                .in("status", 0, 1, 2)
        );

    }

    @Override
    public List<Task> moveLibrary(int level, int aisle) {
        return stdMapper.selectList(new QueryWrapper<Task>()
                .eq("type", 15)
                .eq("s_level", level)
                .likeRight("s_pos", (aisle / 10) + "%")
                .in("status", 0, 1, 2)
        );
    }

    @Override
    public List<Task> isExitTask(int level, int pos) {
        return stdMapper.selectList(new QueryWrapper<Task>()
                .eq("type", 1)
                .eq("s_pos", pos)
                .in("status", 0, 1, 2, 7)
        );
    }

    @Override
    public List<Task> inboundTask(int level, int liftPos) {
        return stdMapper.selectList(new QueryWrapper<Task>()
                .eq("type", 1)
                .eq("s_pos", liftPos)
                .in("status", 0, 1, 2, 7)
        );
    }


    @Override
    public List<Task> inboundLiftTask(int level, int liftId) {
        String preStr = Constance.BOX_PREFIX + liftId + "-";
        return stdMapper.selectList(new QueryWrapper<Task>()
                .eq("type", 1)
                .eq("s_level", level)
                .like("box_number", preStr + "%")
                //.eq("s_pos", pos)
                .in("status", 0, 1, 2, 7));
    }

    @Override
    public List<Task> inboundLiftTaskSK(int level, int liftId) {
        String preStr = liftId + "-";
        return stdMapper.selectList(new QueryWrapper<Task>()
                .select("type", "s_level", "box_number", "status")
                .eq("type", 1)
                .eq("s_level", level)
                .like("box_number", preStr + "%")
                //.eq("s_pos", pos)
                .in("status", 0, 1, 2, 7));
    }
}
