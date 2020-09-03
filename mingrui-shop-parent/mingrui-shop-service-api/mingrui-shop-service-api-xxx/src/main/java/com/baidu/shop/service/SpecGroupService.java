package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
public interface SpecGroupService {

    @GetMapping(value = "specGroup/getSpecGroupInfo")
    @ApiOperation(value = "通过分类查询规格组")
    Result<List<SpecGroupDTO>> getSpecGroupInfo(SpecGroupDTO specGroupDTO);

    @PostMapping(value = "specGroup/saveSpecGroup")
    @ApiOperation(value = "增加规格组")
    Result<List<SpecGroupDTO>> saveSpecGroup(@Validated({MingruiOperation.Add.class}) @RequestBody SpecGroupDTO specGroupDTO);

    @PutMapping(value = "specGroup/saveSpecGroup")
    @ApiOperation(value = "修改规格组")
    Result<List<SpecGroupDTO>> editSpecGroup(@Validated({MingruiOperation.Update.class}) @RequestBody SpecGroupDTO specGroupDTO);

    @DeleteMapping(value = "specGroup/deleteSpecGroup")
    @ApiOperation(value = "通过分类查询规格组")
    Result<List<SpecGroupDTO>> deleteSpecGroup(Integer id);

}
