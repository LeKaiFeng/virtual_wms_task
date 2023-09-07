package com.lee.database.mk.service.impl;

import com.lee.database.mk.entity.Task;
import com.lee.database.mk.mapper.TaskMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class TaskServiceImplTest {

    @Autowired
    protected TaskMapper taskMapper;

    @Test
    void outboundTaskLF() {
        List<Task> tasks = taskMapper.selectAllByStateAndTypeOrderByELevel(0, 1);
        tasks.forEach(System.out::println);
    }
}