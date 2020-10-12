package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName BrandService
 * @Description: TODO
 * @Author lite
 * @Date 2020/8/31
 * @Version V1.0
 **/
@Api(tags = "品牌接口")
public interface BrandService {


    @ApiOperation(value = "查询品牌")
    @GetMapping(value = "brand/getBrand")
    public Result<PageInfo<BrandEntity>> getBrand(BrandDTO brandDTO);

    @ApiOperation(value = "新增品牌")
    @PostMapping(value = "brand/save")
    public Result<JSONObject> saveBrand(@Validated(MingruiOperation.Add.class) @RequestBody BrandDTO brandDTO);

    @ApiOperation(value = "修改品牌")
    @PutMapping(value = "brand/save")
    public Result<JSONObject> editBrand(@Validated(MingruiOperation.Update.class) @RequestBody BrandDTO brandDTO);

    @ApiOperation(value = "修改品牌")
    @DeleteMapping(value = "brand/delete")
    public Result<JSONObject> deleteBrand(Integer id);


    @ApiOperation(value = "通过分类id查询品牌")
    @GetMapping(value = "brand/getBrandByCategoryId")
    public Result<List<BrandEntity>> getBrandByCategoryId(Integer categoryId);

    @GetMapping(value = "brand/getBrandByBrandIds")
    @ApiOperation(value = "通过品牌id集合获取品牌")
    public Result<List<BrandEntity>> getBrandByBrandIds(@RequestParam String brandIds);

}
