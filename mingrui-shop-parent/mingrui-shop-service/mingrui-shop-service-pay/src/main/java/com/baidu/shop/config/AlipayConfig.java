package com.baidu.shop.config;

import lombok.extern.log4j.Log4j;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */
public class AlipayConfig {

//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016102600766702";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCiZc2l4DmqEc8/KnsaG27RQUE/XXYnY4EF/xg9P/+IeobfY0+W1qU8wEgA7UCZU9fwqmZ44fEgXotaIL2oNOQ3p/IsW3ZHPvxIjIri45oNQpbCPsSTVVOrL2BS3G9XV8YIHXjxrpE+fOa0QoIQik2iKweGX5DE//Rm/C9ysXdDXuulCSZzmIQTugQiCCFXSO5Ji7kLK4NXeT+MCQKv9i4XFrsso1ON8eCzwYtjkG9C3SllXQI9nSzQV7rLtE0b8MfKgnqjGbvH9KpjRCg9tQ08QX+JNb87cx0lmu+9oL5zvUdl7U0C8YaDRTJaDBiRo7NumZ32RCMHd2BYiNpNtF+HAgMBAAECggEAVqn5axIT89x5Ov5Sn4YQR3+JcGVSDocdbbPKnbUviwfIPiJjcN2vZJJAWq5CEREbZgWplc0NvvZ1a22ZiNBhEgqARIeywSatv5Gw9I/9wCHPDm4svWuFsQ8hWlbk3DmtJtuDlYZRYIWLIJt1iZok/+vyohURzu/A38ypDY4DVWHSR6hFTW4k1UJmtllt6vGa0b4oFt0MTPeoWYByN3iZkT/7Q8uPlBm8azAqYw4fr9W6k/XiASnbIoT/+g4PqQz1nhVqm+yp2dqvJgJeO1J4Vn1KSPHu/X6goXkkfDEHv4fZULrTbK6a+8HRnQ4d/ZAHAMd34xUDdboBWtMUElFhEQKBgQDZ94hctTTLjJuf/UkK3+MVvS4o+mD7uJ70jIJxzoheazf/n76wZJXALa9MGL8z/wvhRsYvsQaSPXf5ojKTDWP7cqfb17f94LdVbpas5HvE+RHu9PLLF3KNi1KOJ7dAQnSCVYOkODJHi4IhpBnzXtBkXHTIbhwugmmUe0+CdIIs3QKBgQC+vAV5FSHe4zTZKnCtSzvvVQGv02KNSS7XPE2EoMKyoLfU85FyKnK9vp6CySqokDXdx+QW916TksVEpVsu5pgrDYHe8wffNEh3E0NUVjAxlDQGLRTERhmqq5aV3jDJmKCG7hjKN6oQrNxWatj1MrkKo+QI+IlY8OCYsXapRwZ1swKBgDUvD+fHEz3DbkmobMUpgehRA81d4oIpNyfmjQGl6mJXeQ93c/joYh3FR+uici0Gw2hRc5Q5dqgCNZ8Es6Br+QRmooyi4zMgPLuswzkmewjB2V1cCU4Cx6G/6AHlsPIOBQ20Pe25BJOud68rsmVOsjx6zsgFDf1PNuHTAMCNGyJFAoGBAK6+vljntodpLEMGcgMi2X91Jz16cmE1OlfxpKetTZOwUZwlpwysvUEAAmOagJ28uc0+VMeBzUFxB5DT2k2G8MUI9AJaMM0bLoUBvf7nk/HocR1zcgI8o0lNOYKG9bXNQs08GLQz+XiblqcJ70n+Na/85XAenF9DGNJsTGXcb5unAoGBAJGDvtx6JP7O3AqZ1H546I4HqUdC9+gZhoTt9xuNGBmDsRVQ4ZQXYotu+E9Mg1h9CagISwEoT+FSOrNGq6QAxKmXDmMyOTT+Yml6H90aN5KxfXzrnqoKOK7RccxzWlT48oy4D4pZYjgBI2CLSvBSgm8Q32LaQaGGnR4Z2q15uC+N";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAj7gMCnMRHAcWQIzzCuBzAfodQNzrh1zrgtLoOXYg5vslVJe7d1wIOFtm1WtHFF31hHUheTL8Jsr0Y9KLXTWE6ii9i1/8TFaMDSvi46xIUR188Ye7W2duRTTCF7+3nuBphL8phWSmDs5jCjiHk6cHvMpUSas+imRcXq4XZB3CQl9wgYYpfseqEjucQhPwDrRlWQKPNvTS6pt30dvC7+YwnCD2Za3VAYnD1TDXJaQ8Bdfa2esff3GTyfbyYYOdjBHPpGQ5+BKeY9+bV86q+obfE8NOtjx8l7nUWQMyUILy+eYyEWK3sxLiZvgv7bSpu/bhyPMaMbwzW7cxhDOQXChBVwIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8900/pay/returnNotify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8900/pay/returnUrl";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

