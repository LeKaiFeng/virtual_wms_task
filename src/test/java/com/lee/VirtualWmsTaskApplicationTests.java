package com.lee;

import com.lee.database.dss.service.impl.ResourceTaskServiceImpl;
import com.lee.database.mk.entity.Position;
import com.lee.database.mk.mapper.PositionMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class VirtualWmsTaskApplicationTests {

    @Autowired
    protected ResourceTaskServiceImpl taskService;

    @Test
    public void t1() {
//        AtomicInteger num = new AtomicInteger(1);
//        int num1 = num.getAndIncrement();
//        int num2 = num.incrementAndGet();
//        System.out.println(num1 + " , " + num2);

        AtomicInteger boxNum = new AtomicInteger(1);
        boxNum.set(taskService.initBoxId(210110));
        System.out.println(boxNum.get());
    }

}
