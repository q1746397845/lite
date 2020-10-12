package com.baidu.shop.feign;

import com.baidu.shop.response.GoodsResponse;
import com.baidu.shop.service.ShopElasticsearchService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName ElasticsearchFeign
 * @Description: TODO
 * @Author lite
 * @Date 2020/9/27
 * @Version V1.0
 **/
@FeignClient(value = "search-server")
public interface ElasticsearchFeign extends ShopElasticsearchService {

}
