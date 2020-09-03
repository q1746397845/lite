package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品分类接口")
public interface CategoryService {

    @ApiOperation(value = "通过pid查询商品分类")
    @GetMapping(value = "category/list")
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid);

    @ApiOperation(value = "增加商品分类")
    @PostMapping(value = "category/add")
    //声明哪个组下面的参数参加校验-->当前是校验Update组
    public Result<List<JSONObject>> addCategory(@Validated({MingruiOperation.Add.class}) @RequestBody CategoryEntity categoryEntity);


    @ApiOperation(value = "修改商品分类")
    @PutMapping(value = "category/edit")
    //声明哪个组下面的参数参加校验-->当前是校验新增组
    public Result<List<JSONObject>> editCategory(@Validated({MingruiOperation.Update.class}) @RequestBody CategoryEntity categoryEntity);

    @ApiOperation(value = "删除商品分类")
    @DeleteMapping(value = "category/delete")
    public Result<List<JSONObject>> deleteCategory(Integer id);

    @ApiOperation(value = "查询商品的分类信息")
    @GetMapping(value = "category/getByBrand")
    public Result<List<CategoryEntity>> getCatesByBrand(Integer brandId);
}
