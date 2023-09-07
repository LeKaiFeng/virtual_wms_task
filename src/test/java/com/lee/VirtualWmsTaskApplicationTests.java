package com.lee;

import com.lee.database.mk.entity.Position;
import com.lee.database.mk.mapper.PositionMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class VirtualWmsTaskApplicationTests {

    @Autowired
    protected PositionMapper positionService;

    @Test
    public void t1() {
        List<Position> positions = positionService.selectAllByLevelAndAisle(1, 11);
        positions.forEach(System.out::println);
    }

}
