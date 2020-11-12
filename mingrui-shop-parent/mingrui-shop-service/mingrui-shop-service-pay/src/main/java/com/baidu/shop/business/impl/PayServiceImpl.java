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
import com.baidu.shop.config.UnionPayConfig;
import com.baidu.shop.dto.OrderInfo;
import com.baidu.shop.dto.PayInfDTO;
import com.baidu.shop.feign.OrderFeign;
import com.baidu.shop.unionpay.sdk.*;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
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

    @Override
    public void UnionpayPay(PayInfDTO payInfDTO, String token, HttpServletResponse resp) {


        Map<String, String> map = UnionPayConfig.unionPayParams();

        //获取订单信息
        Result<OrderInfo> orderInfoResult = orderFeign.getOrderInfoByOrderId(payInfDTO.getOrderId());

        if(orderInfoResult.getCode() == 200){

            OrderInfo orderInfo = orderInfoResult.getData();
            map.put("orderId",orderInfo.getOrderId() + "");//订单号
            map.put("txnAmt",orderInfo.getActualPay() + "");//金额

            List<String> titlList = orderInfo.getOrderDetailList().stream().map(orderDetail ->  orderDetail.getTitle()).collect(Collectors.toList());
            String titleStr = String.join(",",titlList);
            titleStr = titleStr.length() > 50 ? titleStr.substring(0,50)+"......." : titleStr;

            map.put("riskRateInfo", "{commodityName="+ titleStr +"}");  //商品名称

        }

        resp.setContentType("text/html; charset="+ DemoBase.encoding);
        /**请求参数设置完毕，以下对请求参数进行签名并生成html表单，将表单写入浏览器跳转打开银联页面**/
        Map<String, String> sign = AcpService.sign(map, DemoBase.encoding);

        String requestFrontUrl = SDKConfig.getConfig().getFrontRequestUrl();//获取请求银联的前台地址：对应属性文件acp_sdk.properties文件中的acpsdk.frontTransUrl

        String html = AcpService.createAutoFormHtml(requestFrontUrl, sign, DemoBase.encoding);//生成自动跳转的Html表单
        LogUtil.writeLog("打印请求HTML，此为请求报文，为联调排查问题的依据："+html);
        //将生成的html写到浏览器中完成自动跳转打开银联支付页面；这里调用signData之后，将html写到浏览器跳转到银联页面之前均不能对html中的表单项的名称和值进行修改，如果修改会导致验签不通过
        try {
            resp.getWriter().write(html);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void UnionPayNotify(HttpServletRequest req,HttpServletResponse resp) throws IOException {

        LogUtil.writeLog("BackRcvResponse接收后台通知开始");

        String encoding = req.getParameter(SDKConstants.param_encoding);
        // 获取银联通知服务器发送的后台通知参数
        Map<String, String> reqParam = UnionPayConfig.getAllRequestParam(req);
        LogUtil.printRequestLog(reqParam);

        //重要！验证签名前不要修改reqParam中的键值对的内容，否则会验签不过
        if (!AcpService.validate(reqParam, encoding)) {
            LogUtil.writeLog("验证签名结果[失败].");
            //验签失败，需解决验签问题

        } else {
            LogUtil.writeLog("验证签名结果[成功].");
            //【注：为了安全验签成功才应该写商户的成功处理逻辑】交易成功，更新商户订单状态

            String orderId =reqParam.get("orderId"); //获取后台通知的数据，其他字段也可用类似方式获取
            String respCode = reqParam.get("respCode");
            //判断respCode=00、A6后，对涉及资金类的交易，请再发起查询接口查询，确定交易成功后更新数据库。

        }
        LogUtil.writeLog("BackRcvResponse接收后台通知结束");
        //返回给银联服务器http 200  状态码
        resp.getWriter().print("ok");
    }

    @Override
    public void UnionPayReturnUrl(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        LogUtil.writeLog("FrontRcvResponse前台接收报文返回开始");

        String encoding = req.getParameter(SDKConstants.param_encoding);
        LogUtil.writeLog("返回报文中encoding=[" + encoding + "]");
        String pageResult = "";
        if (DemoBase.encoding.equalsIgnoreCase(encoding)) {
            pageResult = "/utf8_result.jsp";
        } else {
            pageResult = "/gbk_result.jsp";
        }
        Map<String, String> respParam = UnionPayConfig.getAllRequestParam(req);

        // 打印请求报文
        LogUtil.printRequestLog(respParam);

        Map<String, String> valideData = null;
        StringBuffer page = new StringBuffer();
        if (null != respParam && !respParam.isEmpty()) {
            Iterator<Map.Entry<String, String>> it = respParam.entrySet()
                    .iterator();
            valideData = new HashMap<String, String>(respParam.size());
            while (it.hasNext()) {
                Map.Entry<String, String> e = it.next();
                String key = (String) e.getKey();
                String value = (String) e.getValue();
                value = new String(value.getBytes(encoding), encoding);
                page.append("<tr><td width=\"30%\" align=\"right\">" + key
                        + "(" + key + ")</td><td>" + value + "</td></tr>");
                valideData.put(key, value);
            }
        }
        if (!AcpService.validate(valideData, encoding)) {
            page.append("<tr><td width=\"30%\" align=\"right\">验证签名结果</td><td>失败</td></tr>");
            LogUtil.writeLog("验证签名结果[失败].");
        } else {
            page.append("<tr><td width=\"30%\" align=\"right\">验证签名结果</td><td>成功</td></tr>");
            LogUtil.writeLog("验证签名结果[成功].");
            System.out.println(valideData.get("orderId")); //其他字段也可用类似方式获取

            String respCode = valideData.get("respCode");
            //判断respCode=00、A6后，对涉及资金类的交易，请再发起查询接口查询，确定交易成功后更新数据库。
            //修改订单状态,修改为已支付

            orderFeign.updateOrderStatus(respParam.get("orderId"));

            resp.sendRedirect("http://www.mrshop.com/success.html?orderId=" + respParam.get("orderId") + "&totalPay=" + Double.valueOf(respParam.get("txnAmt"))/100);
        }

        req.setAttribute("result", page.toString());
        req.getRequestDispatcher(pageResult).forward(req, resp);

        LogUtil.writeLog("FrontRcvResponse前台接收报文返回结束");
    }



}
