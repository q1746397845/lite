package com.baidu.serviceImpl;

import com.baidu.feign.CategoryFeign;
import com.baidu.feign.GoodsFeign;
import com.baidu.feign.SpecificationFeign;
import com.baidu.service.PageService;
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
import com.baidu.shop.util.ObjectUtil;
import feign.Feign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName PageServiceImpl
 * @Description: TODO
 * @Author lite
 * @Date 2020/9/23
 * @Version V1.0
 **/
@Service
public class PageServiceImpl implements PageService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private CategoryFeign categoryFeign;

    @Autowired
    private SpecificationFeign specificationFeign;



    @Override
    public Map<String, Object> getGoodsInfo(Integer spuId) {

        Map<String, Object> map = new HashMap<>();
        if(ObjectUtil.isNotNull(spuId)){

            SpuDTO spuDTO = new SpuDTO();
            spuDTO.setId(spuId);
            Result<List<SpuDTO>> spuResult = goodsFeign.getSpuInfo(spuDTO);

            if(spuResult.getCode() == 200){
                if(spuResult.getData().size() == 1){
                    map.put("spuInfo",spuResult.getData().get(0));
                }
            }

            SpuDTO spuInfo = spuResult.getData().get(0);

            String categoryIds = spuInfo.getCid1() + "," + spuInfo.getCid2() + "," + spuInfo.getCid3();
            Result<List<CategoryEntity>> categoryResult = categoryFeign.getCategoryByCategoryIds(categoryIds);

            if(categoryResult.getCode() == 200){
                map.put("categoryList",categoryResult.getData());
            }


            SpecParamDTO specParamDTO = new SpecParamDTO();
            specParamDTO.setCid(spuInfo.getCid3());
            specParamDTO.setGeneric(false);
            Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParamInfo(specParamDTO);
            if(specParamResult.getCode() == 200){
                Map<Integer, String> specParamMap = new HashMap<>();
                specParamResult.getData().stream().forEach(specParam ->{
                    specParamMap.put(specParam.getId(),specParam.getName());
                    map.put("specParamMap",specParamMap);
                });
            }

            Result<SpuDetailEntity> spuDetailResult = goodsFeign.getSpuDetailById(spuInfo.getId());
            if (spuDetailResult.getCode() == 200){
                map.put("spuDetail",spuDetailResult.getData());
            }


            Result<List<SkuDTO>> skuBySpuResult = goodsFeign.getSkuBySpuId(spuInfo.getId());
            if(skuBySpuResult.getCode() == 200){
                map.put("skuList",skuBySpuResult.getData());
            }


            SpecGroupDTO specGroupDTO = new SpecGroupDTO();
            specGroupDTO.setCid(spuInfo.getCid3());
            Result<List<SpecGroupEntity>> specGroupResult= specificationFeign.getSpecGroupInfo(specGroupDTO);

            if(specGroupResult.getCode() == 200){
                List<SpecGroupEntity> specGroupList = specGroupResult.getData();
                List<SpecGroupDTO> groupsInParams = specGroupList.stream().map(specGroup -> {
                    SpecGroupDTO sgd = BaiduBeanUtil.copyProperties(specGroup, SpecGroupDTO.class);

                    SpecParamDTO specParamDTO1 = new SpecParamDTO();
                    specParamDTO1.setGeneric(true);
                    specParamDTO1.setGroupId(specGroup.getId());
                    Result<List<SpecParamEntity>> specParamResult1 = specificationFeign.getSpecParamInfo(specParamDTO1);
                    if (specParamResult1.getCode() == 200) {
                        sgd.setSpecParams(specParamResult1.getData());
                    }
                    return sgd;
                }).collect(Collectors.toList());
                map.put("groupsInParams",groupsInParams);
            }



            return map;
        }
        return null;
    }
}
