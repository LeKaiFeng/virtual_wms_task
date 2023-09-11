package com.lee;

import com.lee.database.mk.entity.Position;
import com.lee.database.mk.mapper.PositionMapper;
import com.lee.database.std.entity.Announce;
import com.lee.database.std.service.impl.StdAnnounceServiceImpl;
import com.lee.util.STDTaskTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class VirtualWmsTaskApplicationTests {


    @Autowired
    protected StdAnnounceServiceImpl announceService;

    @Test
    public void t1() {
//        AtomicInteger num = new AtomicInteger(1);
//        int num1 = num.getAndIncrement();
//        int num2 = num.incrementAndGet();
//        System.out.println(num1 + " , " + num2);
        announceService.query().select("id", "box_number").list().forEach(ann -> {
            System.out.println(ann.getId() + " , " + ann.getBoxNumber());
        });
    }

}
