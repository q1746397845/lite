package com.baidu.shop.business.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.business.PayService;
import com.baidu.shop.config.AlipayConfig;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.OrderInfo;
import com.baidu.shop.dto.PayInfDTO;
import com.baidu.shop.feign.OrderFeign;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName PayServiceImpl
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/22
 * @Version V1.0
 **/
@Controller
public class PayServiceImpl extends BaseApiService implements PayService {

    @Resource
    private JwtConfig jwtConfig;

    @Resource
    private OrderFeign orderFeign;

    @Override
    public void requestPay(PayInfDTO payInfDTO, String token, HttpServletResponse response) {
        try {
            //获得初始化的AlipayClient
            AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);

            //设置请求参数
            AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
            alipayRequest.setReturnUrl(AlipayConfig.return_url);
            alipayRequest.setNotifyUrl(AlipayConfig.notify_url);

            Result<OrderInfo> orderInfoResult = orderFeign.getOrderInfoByOrderId(payInfDTO.getOrderId());
            if(orderInfoResult.getCode() == 200){

                OrderInfo orderInfo = orderInfoResult.getData();
                List<String> titlList = orderInfo.getOrderDetailList().stream().map(orderDetail ->  orderDetail.getTitle()).collect(Collectors.toList());

                String titleStr = String.join(",",titlList);
                titleStr = titleStr.length() > 50 ? titleStr.substring(0,50) : titleStr;

                //商户订单号，商户网站订单系统中唯一订单号，必填
                String out_trade_no = payInfDTO.getOrderId() + "";
                //付款金额，必填
                String total_amount = Double.valueOf(orderInfo.getActualPay())/100  + "";
                //订单名称，必填
                String subject = titleStr;
                //商品描述，可空
                String body = payInfDTO.getDescription();

                alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                        + "\"total_amount\":\""+ total_amount +"\","
                        + "\"subject\":\""+ subject +"\","
                        + "\"body\":\""+ body +"\","
                        + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

                //若想给BizContent增加其他可选请求参数，以增加自定义超时时间参数timeout_express来举例说明
                //alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                //		+ "\"total_amount\":\""+ total_amount +"\","
                //		+ "\"subject\":\""+ subject +"\","
                //		+ "\"body\":\""+ body +"\","
                //		+ "\"timeout_express\":\"10m\","
                //		+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
                //请求参数可查阅【电脑网站支付的API文档-alipay.trade.page.pay-请求参数】章节

                //请求
                String result = null;
                result = alipayClient.pageExecute(alipayRequest).getBody();

                //输出

                response.setContentType("text/html; charset=utf-8");

                PrintWriter out = null;
                out = response.getWriter();
                out.println(result);
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void returnNotify(HttpServletRequest httpServletRequest){
        try {
            /* *
             * 功能：支付宝服务器异步通知页面
             * 日期：2017-03-30
             * 说明：
             * 以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
             * 该代码仅供学习和研究支付宝接口使用，只是提供一个参考。


             *************************页面功能说明*************************
             * 创建该页面文件时，请留心该页面文件中无任何HTML代码及空格。
             * 该页面不能在本机电脑测试，请到服务器上做测试。请确保外部可以访问该页面。
             * 如果没有收到该页面返回的 success
             * 建议该页面只做支付成功的业务逻辑处理，退款的处理请以调用退款查询接口的结果为准。
             */

            //获取支付宝POST过来反馈信息
            Map<String,String> params = new HashMap<String,String>();
            Map<String,String[]> requestParams = httpServletRequest.getParameterMap();
            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用
                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
                params.put(name, valueStr);
            }

            boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名

            //——请在这里编写您的程序（以下代码仅作参考）——

            /* 实际验证过程建议商户务必添加以下校验：
            1、需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
            2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
            3、校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）
            4、验证app_id是否为该商户本身。
            */
            if(signVerified) {//验证成功
                //商户订单号
                String out_trade_no = new String(httpServletRequest.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

                //支付宝交易号
                String trade_no = new String(httpServletRequest.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

                //交易状态
                String trade_status = new String(httpServletRequest.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");

                if(trade_status.equals("TRADE_FINISHED")){
                    //判断该笔订单是否在商户网站中已经做过处理
                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    //如果有做过处理，不执行商户的业务程序

                    //注意：
                    //退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                }else if (trade_status.equals("TRADE_SUCCESS")){
                    //判断该笔订单是否在商户网站中已经做过处理
                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    //如果有做过处理，不执行商户的业务程序

                    //注意：
                    //付款完成后，支付宝系统发送该交易状态通知
                }

                //out.println("success");

            }else {//验证失败
                //out.println("fail");

                //调试用，写文本函数记录程序运行情况是否正常
                //String sWord = AlipaySignature.getSignCheckContentV1(params);
                //AlipayConfig.logResult(sWord);
            }

            //——请在这里编写您的程序（以上代码仅作参考）——


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void returnUrl(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            /* *
             * 功能：支付宝服务器同步通知页面
             * 日期：2017-03-30
             * 说明：
             * 以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
             * 该代码仅供学习和研究支付宝接口使用，只是提供一个参考。


             *************************页面功能说明*************************
             * 该页面仅做页面展示，业务逻辑处理请勿在该页面执行
             */

            //获取支付宝GET过来反馈信息
            Map<String,String> params = new HashMap<String,String>();

            Map<String,String[]> requestParams = httpServletRequest.getParameterMap();

            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用
                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
                params.put(name, valueStr);
            }

            boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名

            //——请在这里编写您的程序（以下代码仅作参考）——
            if(signVerified) {
                //商户订单号
                String out_trade_no = new String(httpServletRequest.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

                //支付宝交易号
                String trade_no = new String(httpServletRequest.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

                //付款金额
                String total_amount = new String(httpServletRequest.getParameter("total_amount").getBytes("ISO-8859-1"),"UTF-8");

                //修改订单状态  修改为已支付
                orderFeign.updateOrderStatus(out_trade_no);

                httpServletResponse.sendRedirect("http://www.mrshop.com/success.html?orderId=" + out_trade_no + "&totalPay=" + total_amount);

                //out.println("trade_no:"+trade_no+"<br/>out_trade_no:"+out_trade_no+"<br/>total_amount:"+total_amount);
            }else {
                //out.println("验签失败");
            }
            //——请在这里编写您的程序（以上代码仅作参考）——
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

    }
}
