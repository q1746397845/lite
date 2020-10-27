package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.Car;
import feign.RequestLine;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName CarService
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/19
 * @Version V1.0
 **/
@Api(tags = "购物车接口")
public interface CarService {


    @ApiOperation(value = "添加商品到购物车")
    @PostMapping(value = "car/addCar")
    Result<JSONObject> addCar(@RequestBody Car car,@CookieValue(value = "MRSHOP_TOKEN") String token);

    @ApiOperation(value = "合并购物车")
    @PostMapping(value = "car/mergeCar")
    Result<JSONObject> mergeCar(@RequestBody String clientCarList,@CookieValue(value = "MRSHOP_TOKEN") String token);

    @GetMapping(value = "car/getUserGoodsCar")
    @ApiOperation(value = "从redis获取购物车数据")
    Result<JSONObject> getUserGoodsCar(@CookieValue(value = "MRSHOP_TOKEN") String token);

    @GetMapping(value = "car/carNumUpdate")
    @ApiOperation(value = "修改购物车里的商品数量")
    Result<JSONObject> carNumUpdate(Integer type,Long skuId,@CookieValue(value = "MRSHOP_TOKEN") String token);

    @DeleteMapping(value = "car/delCarData")
    @ApiOperation(value = "删除购物车里的商品")
    Result<JSONObject> delCarData(String skuIds, @CookieValue(value = "MRSHOP_TOKEN") String token);

    @GetMapping(value = "car/getUserDelGoodsCar")
    @ApiOperation(value = "从redis获取删除购物车数据")
    Result<JSONObject> getUserDelGoodsCar(@CookieValue(value = "MRSHOP_TOKEN") String token);

    @ApiOperation(value = "把redis里的用户删除的购物车商品重新添加到购物车里")
    @PostMapping(value = "car/addUserGoodsCarAndDelUserDelGoodsCar")
    Result<JSONObject> addUserGoodsCarAndDelUserDelGoodsCar(@RequestBody Car car,@CookieValue(value = "MRSHOP_TOKEN") String token);
}


