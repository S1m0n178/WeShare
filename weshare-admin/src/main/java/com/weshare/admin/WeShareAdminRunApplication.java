package com.weshare.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(scanBasePackages = {"com.weshare"},exclude = {DataSourceAutoConfiguration.class})
public class WeShareAdminRunApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeShareAdminRunApplication.class,args);
    }
}
