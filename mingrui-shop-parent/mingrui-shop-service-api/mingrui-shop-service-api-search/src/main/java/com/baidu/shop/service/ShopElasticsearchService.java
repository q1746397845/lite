package com.baidu.shop.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.DTO.SearchDTO;
import com.baidu.shop.base.Result;
import com.baidu.shop.response.GoodsResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName ShopElasticsearchService
 * @Description: TODO
 * @Author lite
 * @Date 2020/9/16
 * @Version V1.0
 **/
@Api(tags = "es接口")
public interface ShopElasticsearchService {

//    @ApiOperation(value = "获取商品信息测试")
//    @GetMapping(value = "es/goodsInfo")
//    Result<JSONObject> esGoodsInfo();

    @GetMapping(value = "es/initEsData")
    @ApiOperation(value = "初始化es数据")
    Result<JSONObject> initEsData();

    @GetMapping(value = "es/saveEsData")
    @ApiOperation(value = "新增es数据")
    Result<JSONObject> saveEsData(@RequestParam Integer spuId);

    @GetMapping(value = "es/clearEsData")
    @ApiOperation(value = "清空es中的商品数据")
    Result<JSONObject> clearEsData();

    @GetMapping(value = "es/search")
    @ApiOperation(value = "搜索")
    GoodsResponse search(SearchDTO searchDTO);

    @GetMapping(value = "es/deleteEsData")
    @ApiOperation(value = "删除es中的商品数据")
    Result<JSONObject> deleteEsData(@RequestParam Integer spuId);
}
