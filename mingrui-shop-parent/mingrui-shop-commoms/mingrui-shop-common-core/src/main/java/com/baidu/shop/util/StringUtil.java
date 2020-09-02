package com.baidu.shop.util;

import org.springframework.util.StringUtils;

/**
 * @ClassName StringUtil
 * @Description: TODO
 * @Author lite
 * @Date 2020/8/31
 * @Version V1.0
 **/
public class StringUtil {

    public static Boolean isEmpty(String str){
        return str == null || "".equals(str);
    }

    public static Boolean isNotEmpty(String str){
        return str != null && !"".equals(str);
    }

    public static Integer toInteger(String str){
        if(isNotEmpty(str))return Integer.parseInt(str);
        return 0;
    }
}
