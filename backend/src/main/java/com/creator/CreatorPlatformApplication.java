package com.creator;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.creator.mapper")
public class CreatorPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(CreatorPlatformApplication.class, args);
    }
}
