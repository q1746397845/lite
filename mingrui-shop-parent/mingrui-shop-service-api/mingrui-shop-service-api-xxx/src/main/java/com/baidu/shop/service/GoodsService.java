package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpuEntity;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Api(tags = "商品接口")
public interface GoodsService {

    @ApiOperation("获取spu信息")
    @GetMapping(value = "goods/getSpuInfo")
    public Result<PageInfo<SpuDTO>> getSpuInfo(SpuDTO spuDTO);

    @ApiOperation("新建商品")
    @PostMapping(value = "goods/saveGoods")
    public Result<PageInfo<SpuDTO>> saveGoods(@RequestBody SpuDTO spuDTO);
}
