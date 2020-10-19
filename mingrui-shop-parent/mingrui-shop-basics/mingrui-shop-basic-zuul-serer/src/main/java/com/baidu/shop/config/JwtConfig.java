package com.baidu.shop.config;

import com.baidu.shop.util.RsaUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.PostConstruct;
import java.security.PublicKey;
import java.util.List;

/**
 * @ClassName JwtConfig
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/17
 * @Version V1.0
 **/
@Data
@Configuration
public class JwtConfig {

    @Value("${mrshop.jwt.pubkeyPath}")
    private String pubKeyPath;//公匙地址

    @Value("${mrshop.jwt.cookieName}")
    private String cookieName;

    @Value("#{'${mrshop.filter.excludes}'.split(',')}")
    private List<String> excludePath;//用#{}代表要使用springEl表达式

    private PublicKey publicKey;//公匙

    private static final Logger logger = LoggerFactory.getLogger(JwtConfig.class);

    @PostConstruct
    public void init(){
        try {
            // 获取公钥和私钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            logger.error("初始化公钥失败！", e);
            throw new RuntimeException();
        }
    }
}
