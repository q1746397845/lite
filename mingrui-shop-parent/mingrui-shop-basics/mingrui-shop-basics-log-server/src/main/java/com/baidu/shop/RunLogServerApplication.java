package com.baidu.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @ClassName RunLogServerApplication
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/27
 * @Version V1.0
 **/
@SpringBootApplication
@EnableEurekaClient
@MapperScan(value = "com.baidu.shop.mapper")
public class RunLogServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RunLogServerApplication.class);
    }
}

