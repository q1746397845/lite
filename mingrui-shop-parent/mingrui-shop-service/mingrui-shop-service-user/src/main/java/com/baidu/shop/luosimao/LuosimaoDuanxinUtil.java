package com.baidu.shop.luosimao;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;

/**
 * @ClassName LuosimaoDuanxinUtil
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/13
 * @Version V1.0
 **/
public class LuosimaoDuanxinUtil {

    static Logger logger =  LoggerFactory.getLogger(LuosimaoDuanxinUtil.class);

    //短信平台的APIkey
    private static final String DUANXIN_API_KEY = "key-4b97e82d24a41b0be8db8c50e6f2a229";//需要使用自己的APIkey
    private static final String SPEAK_API_KEY = "key-bd85acffccc3d4bac192d9b75dee35a5";//需要使用自己的APIkey
    private static final String SEND_DUANXIN_URL = "http://sms-api.luosimao.com/v1/send.json";//发送短信的接口
    private static final String SEND_SPEAK_URL = "http://voice-api.luosimao.com/v1/verify.json";//发送语音的接口
    private static final String STATUS_URL = "http://sms-api.luosimao.com/v1/status.json";//查看余额的接口

    /**
     * 发送短信
     * @param phone
     * @param code
     * @return
     */
    public static String SendCode(String phone,String code){
        // just replace key here
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter(
                "api",DUANXIN_API_KEY));
        WebResource webResource = client.resource(SEND_DUANXIN_URL);
        MultivaluedMapImpl formData = new MultivaluedMapImpl();
        formData.add("mobile", phone);
        formData.add("message", "验证码：" + code + "【铁壳测试】");//注意此处不能修改
        ClientResponse response =  webResource.type(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)).
                post(ClientResponse.class, formData);
        String textEntity = response.getEntity(String.class);
        int status = response.getStatus();
        logger.info(textEntity);
        logger.info("---------发送短信验证状态------" + status);
        return textEntity;
    }

    public static String sendSpeak(String phone,String code){
        // just replace key here
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter(
                "api",SPEAK_API_KEY));
        WebResource webResource = client.resource(
                SEND_SPEAK_URL);
        MultivaluedMapImpl formData = new MultivaluedMapImpl();
        formData.add("mobile", phone);
        formData.add("code", code);
        ClientResponse response =  webResource.type( MediaType.APPLICATION_FORM_URLENCODED ).
                post(ClientResponse.class, formData);
        String textEntity = response.getEntity(String.class);
        int status = response.getStatus();
        logger.info(textEntity);
        logger.info("---------发送语音验证状态------" + status);

        return textEntity;
    }

    /**
     * 查看余额
     * @return
     */
    private static String getStatus(){
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter(
                "api",DUANXIN_API_KEY));
        WebResource webResource = client.resource( STATUS_URL );
        MultivaluedMapImpl formData = new MultivaluedMapImpl();
        ClientResponse response =  webResource.get( ClientResponse.class );
        String textEntity = response.getEntity(String.class);
        int status = response.getStatus();

        logger.info(textEntity);
        logger.info(status + "");
        return textEntity;
    }
}
