package com.baidu.shop.web;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.business.UserOauthService;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.util.CookieUtils;
import com.baidu.shop.util.JwtUtils;
import com.baidu.shop.util.StringUtil;
import com.google.gson.JsonObject;
import io.jsonwebtoken.Jwt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName UserOauthController
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/15
 * @Version V1.0
 **/
@RestController
@Api(value = "用户认证接口")
public class UserOauthController  extends BaseApiService {


    @Autowired
    private UserOauthService userOauthService;

    @Autowired
    private JwtConfig jwtConfig;

    @PostMapping(value = "oauth/login")
    @ApiOperation(value = "用户登录")
    public Result<JsonObject> login(@RequestBody UserDTO userDTO, HttpServletRequest request, HttpServletResponse response){

        String token = userOauthService.checkUser(userDTO,jwtConfig);
        if(StringUtil.isEmpty(token)){
            return this.setResultError(HTTPStatus.VALID_USER_PASSWORD_ERROR,"用户名或密码错误");
        }
        CookieUtils.setCookie(request,response,jwtConfig.getCookieName(),token,jwtConfig.getCookieMaxAge(),true);
        return this.setResultSuccess();
    }

    @GetMapping(value = "oauth/verify")
    public Result<UserInfo> checkUserIsLogin(@CookieValue(value = "MRSHOP_TOKEN") String token,HttpServletRequest request, HttpServletResponse response){

        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            //可以解析token的证明用户是正确登录状态,重新生成token,这样登录状态就又刷新30分钟过期了
            String newToken = JwtUtils.generateToken(userInfo, jwtConfig.getPrivateKey(), jwtConfig.getExpire());

            //将新token写入cookie,过期时间延长了
            CookieUtils.setCookie(request,response,jwtConfig.getCookieName(),newToken,jwtConfig.getCookieMaxAge(),true);

            return this.setResultSuccess(userInfo);
        } catch (Exception e) {//如果有异常 说明token有问题
            //e.printStackTrace();
            return this.setResultError(HTTPStatus.VERIFY_ERROR,"用户失效");
        }
    }
}
