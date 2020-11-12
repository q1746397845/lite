package com.baidu.shop.business;

/**
 * @ClassName PayService
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/22
 * @Version V1.0
 **/

import com.baidu.shop.dto.PayInfDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Api(tags = "支付接口")
public interface PayService {

    @ApiOperation(value = "请求支付")
    @GetMapping(value = "pay/requestPay")
    void requestPay(PayInfDTO payInfDTO, @CookieValue(value = "MRSHOP_TOKEN") String token, HttpServletResponse response);

    @ApiOperation(value = "通知接口,这个可能暂时测试不了")
    @GetMapping(value = "pay/returnNotify")
    void returnNotify(HttpServletRequest httpServletRequest) throws UnsupportedEncodingException;

    @ApiOperation(value = "支付宝跳转成功页面接口")
    @GetMapping(value = "pay/returnUrl")
    void returnUrl(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse);


    @ApiOperation(value = "银联支付")
    @GetMapping(value = "pay/UnionpayPay")
    void UnionpayPay(PayInfDTO payInfDTO, @CookieValue(value = "MRSHOP_TOKEN") String token,HttpServletResponse resp);

    @ApiOperation(value = "银联通知接口,可以通过内网穿透实现通知")
    @PostMapping(value = "pay/UnionPayNotify")
    void UnionPayNotify(HttpServletRequest req,HttpServletResponse resp) throws IOException;

    @ApiOperation(value = "银联跳转成功页面接口")
    @PostMapping(value = "pay/UnionPayReturnUrl")
    void UnionPayReturnUrl(HttpServletRequest req,HttpServletResponse resp) throws IOException, ServletException;
}
