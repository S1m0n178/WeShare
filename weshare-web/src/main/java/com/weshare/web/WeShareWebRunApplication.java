package com.weshare.web;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(scanBasePackages = {"com.weshare"})
@MapperScan(basePackages= {"com.weshare.mappers"})
public class WeShareWebRunApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeShareWebRunApplication.class,args);
    }
}
