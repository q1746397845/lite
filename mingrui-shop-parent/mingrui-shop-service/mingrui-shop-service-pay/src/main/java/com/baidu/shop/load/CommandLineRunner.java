package com.baidu.shop.load;

import com.baidu.shop.unionpay.sdk.SDKConfig;
import org.springframework.stereotype.Component;

/**
 * @ClassName CommandLineRunner
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/29
 * @Version V1.0
 **/
@Component
public class CommandLineRunner implements org.springframework.boot.CommandLineRunner {


    //预先需要加载的数据
    @Override
    public void run(String... args) throws Exception {
        SDKConfig.getConfig().loadPropertiesFromSrc();
        System.out.println(">>>>>>>>>>>>服务启动成功,执行加载数据等操作<<<<<<<<<<<<<<<<<<<");
    }
}
