package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.Car;
import com.baidu.shop.repository.RedisRepository;
import com.baidu.shop.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName CarServiceImpl
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/19
 * @Version V1.0
 **/
@RestController
public class CarServiceImpl extends BaseApiService implements CarService {

    @Autowired
    private RedisRepository redisRepository;
    @Override
    public Result<JSONObject> addCar(Car car) {
        System.out.println("=============");
        return this.setResultSuccess();
    }
}
