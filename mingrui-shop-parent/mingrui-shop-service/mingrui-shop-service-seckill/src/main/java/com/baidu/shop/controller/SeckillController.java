package com.baidu.shop.controller;

import com.baidu.shop.service.SeckillService;
import com.sun.istack.internal.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @ClassName SeckillController
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/31
 * @Version V1.0
 **/
@RestController
@RequestMapping
@Slf4j
public class SeckillController {

    @Resource
    private SeckillService seckillService;

    @GetMapping("/createWrongOrder/{sid}")
    public String createWrongOrder(@PathVariable int sid){
        log.info("购买物品编号sid=[{}]",sid);
        int id = 0;
        id = seckillService.createWrongOrder(sid);
        log.info("创建订单id:[{}]",id);

        return String.valueOf(id);
    }
}
