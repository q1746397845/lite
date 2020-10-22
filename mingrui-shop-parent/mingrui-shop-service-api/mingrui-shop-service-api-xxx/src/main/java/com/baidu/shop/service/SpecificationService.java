package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName SpecGroupService
 * @Description: TODO
 * @Author lite
 * @Date 2020/9/3
 * @Version V1.0
 **/
@Api(tags = "规格接口")
public interface SpecificationService {

    @GetMapping(value = "specGroup/getSpecGroupInfo")
    @ApiOperation(value = "通过分类查询规格组")
    Result<List<SpecGroupEntity>> getSpecGroupInfo(@SpringQueryMap SpecGroupDTO specGroupDTO);

    @PostMapping(value = "specGroup/saveSpecGroup")
    @ApiOperation(value = "增加规格组")
    Result<List<JSONObject>> saveSpecGroup(@Validated({MingruiOperation.Add.class}) @RequestBody SpecGroupDTO specGroupDTO);

    @PutMapping(value = "specGroup/saveSpecGroup")
    @ApiOperation(value = "修改规格组")
    Result<List<JSONObject>> editSpecGroup(@Validated({MingruiOperation.Update.class}) @RequestBody SpecGroupDTO specGroupDTO);

    @DeleteMapping(value = "specGroup/deleteSpecGroup")
    @ApiOperation(value = "删除规格组")
    Result<List<JSONObject>> deleteSpecGroup(Integer id);



    @GetMapping(value = "specParam/getSpecParamInfo")
    @ApiOperation(value = "查询规格参数")
    Result<List<SpecParamEntity>> getSpecParamInfo(@SpringQueryMap SpecParamDTO specParamDTO);

    @PostMapping(value = "specParam/saveSpecParam")
    @ApiOperation(value = "增加规格组参数")
    Result<List<JSONObject>> saveSpecParam(@Validated({MingruiOperation.Add.class}) @RequestBody SpecParamDTO specParamDTO);

    @PutMapping(value = "specParam/saveSpecParam")
    @ApiOperation(value = "修改规格组参数")
    Result<List<JSONObject>> editSpecParam(@Validated({MingruiOperation.Update.class}) @RequestBody SpecParamDTO specParamDTO);

    @DeleteMapping(value = "specParam/deleteSpecParam")
    @ApiOperation(value = "删除规格组参数")
    Result<List<JSONObject>> deleteSpecParam(Integer id);

    @GetMapping(value = "specParam/getSpecParamById")
    @ApiOperation(value = "通过规格参数id查询规格参数")
    Result<SpecParamEntity> getSpecParamById(@RequestParam Integer id);

}
