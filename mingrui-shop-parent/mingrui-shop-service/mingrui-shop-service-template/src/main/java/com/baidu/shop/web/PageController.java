package com.baidu.shop.web;

import com.baidu.shop.service.PageService;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @ClassName PageController
 * @Description: TODO
 * @Author lite
 * @Date 2020/9/23
 * @Version V1.0
 **/
//@Controller
//@RequestMapping(value = "item")
public class PageController {

    //@Autowired
    private PageService pageService;

    //@GetMapping(value = "{spuId}.html")
    public String getGoodsInfo(@PathVariable(value = "spuId") Integer spuId, ModelMap modelMap){

        Map<String,Object> map = pageService.getGoodsInfo(spuId);

        modelMap.putAll(map);
        return "item";
    }
}
