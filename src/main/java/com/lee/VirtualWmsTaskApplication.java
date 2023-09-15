package com.lee;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.lee.database.*.mapper")
@SpringBootApplication
@EnableScheduling
public class VirtualWmsTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(VirtualWmsTaskApplication.class, args);
    }


}
