package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName TemplateService
 * @Description: TODO
 * @Author lite
 * @Date 2020/9/25
 * @Version V1.0
 **/
@Api(value = "商品静态化")
public interface TemplateService {

    @GetMapping(value = "template/createStaticHTMLTemplate")
    @ApiOperation(value = "通过spuId生成静态HTML文件")
    Result<JSONObject> createStaticHTMLTemplate(@RequestParam Integer spuId);

    @GetMapping(value = "template/initStaticHTMLTemplate")
    @ApiOperation(value = "初始化静态HTML文件")
    Result<JSONObject> initStaticHTMLTemplate();

    @DeleteMapping(value = "template/deleteStaticHTMLTemplate")
    @ApiOperation(value = "通过spuId删除静态HTML文件")
    Result<JSONObject> deleteHTMLTemplate(Integer spuId);

}
