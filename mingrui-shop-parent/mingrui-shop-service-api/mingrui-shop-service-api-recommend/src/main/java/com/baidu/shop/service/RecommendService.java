package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrowsingDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface RecommendService {

    @PostMapping(value = "recommend/saveBrowsing")
    Result<JSONObject> saveBrowsing(@RequestBody BrowsingDTO browsingDTO,@CookieValue(value = "MRSHOP_TOKEN") String token);

    @GetMapping(value = "recommend/getBrowsing")
    Result<List<BrowsingDTO>> getBrowsing(@CookieValue(value = "MRSHOP_TOKEN") String token);

    @GetMapping(value = "recommend/getGoodsInfo")
    Result<List<BrowsingDTO>> getGoodsInfo();
}
