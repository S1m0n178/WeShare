package com.weshare.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(scanBasePackages = {"com.weshare"},exclude = {DataSourceAutoConfiguration.class})
public class WeShareWebRunApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeShareWebRunApplication.class,args);
    }
}
