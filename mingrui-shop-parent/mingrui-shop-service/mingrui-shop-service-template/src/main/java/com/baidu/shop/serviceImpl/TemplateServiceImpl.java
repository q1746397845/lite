package com.baidu.shop.serviceImpl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.feign.CategoryFeign;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecificationFeign;
import com.baidu.shop.service.TemplateService;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.util.BaiduBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName TemplateServiceImpl
 * @Description: TODO
 * @Author lite
 * @Date 2020/9/25
 * @Version V1.0
 **/
@RestController
public class TemplateServiceImpl extends BaseApiService implements TemplateService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private CategoryFeign categoryFeign;

    @Autowired
    private SpecificationFeign specificationFeign;

    //注入静态化模板
    @Autowired
    private TemplateEngine templateEngine;

    //静态文件生成路径
    @Value(value = "${mrshop.static.html.path}")
    private String staticHTMLPath;

    @Override
    public Result<JSONObject> createStaticHTMLTemplate(Integer spuId) {


        Map<String, Object> map = this.getGoodsInfo(spuId);
        //创建模板引擎上下文
        Context context = new Context();

        //将所有的数据放到模板中
        context.setVariables(map);

        //创建文件 param1:文件路径 param2:文件名称
        File file = new File(staticHTMLPath, spuId + ".html");

        //创建文件输出流
        PrintWriter writer = null;
        try {
            writer =  new PrintWriter(file,"UTF-8");
            //根据模板生成静态文件
            //param1:模板名称 params2:模板上下文[上下文中包含了需要填充的数据],文件输出流
            templateEngine.process("item",context,writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> initStaticHTMLTemplate() {
        Result<List<SpuDTO>> spuInfo = goodsFeign.getSpuInfo(new SpuDTO());
        if(spuInfo.getCode() == 200){
            List<SpuDTO> spuDTO = spuInfo.getData();
            spuDTO.stream().forEach(spu ->{
                this.createStaticHTMLTemplate(spu.getId());
            });

        }
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> deleteHTMLTemplate(Integer spuId) {
        File file = new File(staticHTMLPath+ File.separator + spuId + ".html");
        if(file.exists()){
            file.delete();
            return this.setResultSuccess();
        }else{
            return this.setResultError("文件删除失败");
        }
    }

    /**
     * 获取商品页面展示信息
     * @param spuId
     * @return
     */
    private Map<String, Object> getGoodsInfo(Integer spuId) {

        Map<String, Object> map = new HashMap<>();

        //获取SpuDTO信息
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);
        Result<List<SpuDTO>> spuResult = goodsFeign.getSpuInfo(spuDTO);
        if(spuResult.getCode() == 200){
            if(spuResult.getData().size() == 1){
                map.put("spuInfo",spuResult.getData().get(0));
            }
        }


        //获取分类信息
        SpuDTO spuInfo = spuResult.getData().get(0);
        String categoryIds = spuInfo.getCid1() + "," + spuInfo.getCid2() + "," + spuInfo.getCid3();
        Result<List<CategoryEntity>> categoryResult = categoryFeign.getCategoryByCategoryIds(categoryIds);
        if(categoryResult.getCode() == 200){
            map.put("categoryList",categoryResult.getData());
        }


        Map<Integer, String> specParamMap = this.getSpecialSpec(spuInfo.getCid3());
        if(null != specParamMap){
            map.put("specParamMap",specParamMap);
        }


        //通过spuId 获取 spuDetail
        Result<SpuDetailEntity> spuDetailResult = goodsFeign.getSpuDetailById(spuInfo.getId());
        if (spuDetailResult.getCode() == 200){
            map.put("spuDetail",spuDetailResult.getData());
        }


        //通过spuId获取sku list 集合
        Result<List<SkuDTO>> skuBySpuResult = goodsFeign.getSkuBySpuId(spuInfo.getId());
        if(skuBySpuResult.getCode() == 200){
            map.put("skuList",skuBySpuResult.getData());
        }


        List<SpecGroupDTO> groupsInParams = this.getGroupsInParams(spuInfo.getCid3());
        if(null != groupsInParams){
            map.put("groupsInParams",groupsInParams);
        }
        return map;
    }


    /**
     * 获取特有参数
     * @return
     */
    private Map<Integer, String> getSpecialSpec(Integer categoryId){

        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(categoryId);
        specParamDTO.setGeneric(false);

        Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParamInfo(specParamDTO);

        if(specParamResult.getCode() == 200){
            Map<Integer, String> specParamMap = new HashMap<>();
            specParamResult.getData().stream().forEach(specParam ->{
                specParamMap.put(specParam.getId(),specParam.getName());
            });
            return specParamMap;
        }
        return null;
    }



    /**
     * 获取规格组和该规格组下的通用参数
     * @param categoryId
     */
    private List<SpecGroupDTO> getGroupsInParams(Integer categoryId) {
        SpecGroupDTO specGroupDTO = new SpecGroupDTO();
        specGroupDTO.setCid(categoryId);
        //通过分类id查询规格组
        Result<List<SpecGroupEntity>> specGroupResult= specificationFeign.getSpecGroupInfo(specGroupDTO);
        if(specGroupResult.getCode() == 200){
            List<SpecGroupEntity> specGroupList = specGroupResult.getData();
            List<SpecGroupDTO> groupsInParams = specGroupList.stream().map(specGroup -> {
                SpecGroupDTO sgd = BaiduBeanUtil.copyProperties(specGroup, SpecGroupDTO.class);

                //通过 规格组id 和 是否是通用参数 查询每个规格组下的通用参数
                SpecParamDTO specParamDTO = new SpecParamDTO();
                specParamDTO.setGeneric(true);
                specParamDTO.setGroupId(specGroup.getId());
                Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParamInfo(specParamDTO);

                if (specParamResult.getCode() == 200) {
                    sgd.setSpecParams(specParamResult.getData());
                }
                return sgd;
            }).collect(Collectors.toList());
            return groupsInParams;
        }
        return null;
    }

}
