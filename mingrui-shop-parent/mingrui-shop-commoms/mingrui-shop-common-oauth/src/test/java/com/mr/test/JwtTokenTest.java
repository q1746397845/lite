package com.mr.test;

import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.util.JwtUtils;
import com.baidu.shop.util.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @ClassName JwtTokenTest
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/15
 * @Version V1.0
 **/
public class JwtTokenTest {
    //公钥位置
    private static final String pubKeyPath = "G:\\secret\\rea.pub";
    //私钥位置
    private static final String priKeyPath = "G:\\secret\\rea.pri";
    //公钥对象
    private PublicKey publicKey;
    //私钥对象
    private PrivateKey privateKey;


    /**
     * 生成公钥私钥 根据密文
     * @throws Exception
     */
    @Test
    public void genRsaKey() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "lite");
    }


    /**
     * 从文件中读取公钥私钥
     * @throws Exception
     */
   @Before
    public void getKeyByRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    /**
     * 根据用户信息结合私钥生成token
     * @throws Exception
     */
    @Test
    public void genToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(1, "zhaojunhao"), privateKey, 2);
        System.out.println("user-token = " + token);
    }


    /**
     * 结合公钥解析token
     * @throws Exception
     */
    @Test
    public void parseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJleHAiOjE2MDI3Njc0MDB9.mQ8Ap5ZDV6aunm1z_XQVJbPWyhqCJfU1QPWFQdQoXiA2MyiDKmen0ojsBez2Msogg0dllMOi4RnJu37TKCDpAZJgveSQcXR04oqGzRrZwLSzdN-nYvkNkfycQgmA1q7iFLP2tZBLP-rRwNxTdYEdWs5P9rNPl8S_vaPj8VpOTZE";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}
