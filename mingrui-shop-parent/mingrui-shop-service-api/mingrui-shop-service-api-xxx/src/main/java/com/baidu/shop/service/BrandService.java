package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.util.JSONUtil;
import com.baidu.shop.validate.group.MingruiOperation;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

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
    public Result<PageInfo<BrandDTO>> getBrand(BrandDTO brandDTO);

    @ApiOperation(value = "新增品牌")
    @PostMapping(value = "brand/save")
    public Result<JSONObject> saveBrand(@Validated(MingruiOperation.Add.class) @RequestBody BrandDTO brandDTO);

}
