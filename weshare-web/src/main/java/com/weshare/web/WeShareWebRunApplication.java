package com.weshare.web;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com.weshare"})
@MapperScan(basePackages= {"com.weshare.mappers"})
@EnableTransactionManagement
@EnableScheduling
public class WeShareWebRunApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeShareWebRunApplication.class,args);
    }
}
