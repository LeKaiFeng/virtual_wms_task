package com.lee;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lee.database.dss.entity.ResourceLocation;
import com.lee.database.dss.entity.ResourceTask;
import com.lee.database.dss.mapper.ResourceTaskMapper;
import com.lee.database.dss.service.impl.ResourceLocationServiceImpl;
import com.lee.database.dss.service.impl.ResourceTaskServiceImpl;
import com.lee.database.mk.entity.Position;
import com.lee.database.mk.mapper.PositionMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class VirtualWmsTaskApplicationTests {

    @Autowired
    protected ResourceTaskServiceImpl taskService;

    @Autowired
    protected ResourceTaskMapper taskMapper;

    @Autowired
    protected ResourceLocationServiceImpl locationService;

    @Test
    public void t1() {
//        AtomicInteger num = new AtomicInteger(1);
//        int num1 = num.getAndIncrement();
//        int num2 = num.incrementAndGet();
//        System.out.println(num1 + " , " + num2);

//        AtomicInteger boxNum = new AtomicInteger(1);
//        boxNum.set(taskService.initBoxId(210110));
//        System.out.println(boxNum.get());
//        List<ResourceTask> resourceTasks = taskMapper.selectList(new QueryWrapper<ResourceTask>()
//                .select("DISTINCT start_level")
//                .eq("type", 1)
//                .like("barcode", "BoxLift-1-%")
//                .in("state", 0, 1, 2, 7)
//        );
//        resourceTasks.forEach(System.out::println);

        List<ResourceLocation> resourceLocations = locationService.outLocations(1, Arrays.asList(11));
        resourceLocations.forEach(System.out::println);
    }

}
