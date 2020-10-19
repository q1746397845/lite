package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName UserService
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/13
 * @Version V1.0
 **/
@Api(value = "用户接口")
public interface UserService {

    @ApiOperation(value = "用户注册")
    @PostMapping(value = "user/register")
    Result<JSONObject> register(@Validated(value = {MingruiOperation.Add.class}) @RequestBody UserDTO userDTO);


    @ApiOperation(value = "校验用户名和手机号唯一")
    @GetMapping(value = "user/check/{value}/{type}")
    Result<List<UserEntity>> checkUserNameOrPhone(@PathVariable(value = "value") String value, @PathVariable(value = "type") Integer type);

    @ApiOperation(value = "给手机号发送验证码")
    @PostMapping(value = "user/sendValidCode")
    Result<JSONObject> sendValidCode(@RequestBody UserDTO userDTO);

    @ApiOperation(value = "校验手机验证码")
    @GetMapping(value = "user/checkValidCode")
    Result<JSONObject> checkValidCode(String phone,String code);
}
