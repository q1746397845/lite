package com.baidu.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @ClassName RunSeckillServiceApplication
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/31
 * @Version V1.0
 **/
@SpringBootApplication
@MapperScan(value = "com.baidu.shop.mapper")
@EnableEurekaClient
public class RunSeckillServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunSeckillServiceApplication.class);
    }
}
