package com.baidu.feign;

import com.baidu.shop.service.GoodsService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName GoodsFeign
 * @Description: TODO
 * @Author lite
 * @Date 2020/9/16
 * @Version V1.0
 **/
@FeignClient(contextId = "GoodsService",value = "xxx-server")
public interface GoodsFeign extends GoodsService {
}
