package com.lee;

import com.lee.database.mk.entity.Position;
import com.lee.database.mk.mapper.PositionMapper;
import com.lee.database.std.entity.Announce;
import com.lee.database.std.entity.Locations;
import com.lee.database.std.service.impl.StdAnnounceServiceImpl;
import com.lee.database.std.service.impl.StdLocationsServiceImpl;
import com.lee.util.STDTaskTemplate;
import javafx.scene.effect.Light;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class VirtualWmsTaskApplicationTests {


    @Autowired
    protected StdLocationsServiceImpl locationsService;

    @Test
    public void t1() {
//        AtomicInteger num = new AtomicInteger(1);
//        int num1 = num.getAndIncrement();
//        int num2 = num.incrementAndGet();
//        System.out.println(num1 + " , " + num2);
//        locationsService.query()
//                .select("\"level\"", "pos", "location", "aisle", "type", "state","box_number")
//                .eq("type", 0)
//                .eq("\"level\"",1)
//                .list()
//                .forEach(lt -> {
//                    System.out.println(lt.toString());
//                });
        locationsService.inboundLocations(-1)
    }

}
