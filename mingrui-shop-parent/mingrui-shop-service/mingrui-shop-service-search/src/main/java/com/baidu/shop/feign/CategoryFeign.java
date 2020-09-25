package com.baidu.shop.feign;

import com.baidu.shop.service.CategoryService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName CategoryFeign
 * @Description: TODO
 * @Author lite
 * @Date 2020/9/21
 * @Version V1.0
 **/
@FeignClient(value = "xxx-server",contextId = "CategoryService")
public interface CategoryFeign extends CategoryService {
}
