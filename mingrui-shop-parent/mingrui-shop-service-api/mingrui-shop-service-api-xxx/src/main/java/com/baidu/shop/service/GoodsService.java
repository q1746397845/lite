package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.entity.SpuEntity;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品接口")
public interface GoodsService {

    @ApiOperation("获取spu信息")
    @GetMapping(value = "goods/getSpuInfo")
    public Result<List<JSONObject>> getSpuInfo(SpuDTO spuDTO);

    @ApiOperation("新建商品")
    @PostMapping(value = "goods/saveGoods")
    public Result<List<JSONObject>> saveGoods(@RequestBody SpuDTO spuDTO);


    @ApiOperation("修改商品")
    @PutMapping(value = "goods/saveGoods")
    public Result<List<JSONObject>> editGoods(@RequestBody SpuDTO spuDTO);

    @ApiOperation("删除商品")
    @DeleteMapping(value = "goods/deleteGoods")
    public Result<List<JSONObject>> deleteGoods(Integer spuId);


    @ApiOperation("通过spuId查询skuDetail")
    @GetMapping(value = "goods/getSpuDetailById")
    public Result<SpuDetailEntity> getSpuDetailById(Integer spuId);

    @GetMapping(value = "goods/getSkuBySpuId")
    @ApiOperation("通过spuId查询sku")
    public Result<List<SkuDTO>> getSkuBySpuId(Integer spuId);


    @PutMapping(value = "goods/updateSaleable")
    @ApiOperation("控制商品上下架状态")
    public Result<List<SkuDTO>> updateSaleable(@RequestBody SpuEntity spuEntity);
}
