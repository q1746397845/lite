package com.baidu.shop.feign;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName GoodsFeign
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/23
 * @Version V1.0
 **/
@FeignClient(value = "xxx-server")
public interface GoodsFeign {

    @PutMapping(value = "goods/updateGoodsStock")
    Result<JSONObject> updateGoodsStock(@RequestParam Integer num,@RequestParam Long skuId);

    @PutMapping(value = "goods/addGoodsStock")
    @ApiOperation("恢复商品库存")
    Result<JSONObject> addGoodsStock(@RequestParam Integer num,@RequestParam Long skuId);
}
