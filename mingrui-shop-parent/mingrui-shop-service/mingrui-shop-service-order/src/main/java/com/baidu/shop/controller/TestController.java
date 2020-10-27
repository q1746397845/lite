package com.baidu.shop.controller;

import com.baidu.shop.component.MrRabbitMQ;
import com.baidu.shop.constant.MqMessageConstant;
import com.baidu.shop.consumer.RabbitmqConsumer;
import com.baidu.shop.util.JSONUtil;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName TestController
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/23
 * @Version V1.0
 **/
@RestController
public class TestController {

    @Autowired
    private MrRabbitMQ mrRabbitMQ;

    @GetMapping(value = "test")
    public String test(){
        Map<String, String> map = new HashMap<>();
        HashMap<Long, Integer> map1 = new HashMap<>();
        map1.put(1L,2);
        map.put("1", JSONUtil.toJsonString(map1));
        mrRabbitMQ.send(map,5);

        return "success";
    }
}
