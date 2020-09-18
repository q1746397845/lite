package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecificationFeign;
import com.baidu.shop.service.ShopElasticsearchService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.util.ESHighLightUtil;
import com.baidu.shop.util.JSONUtil;
import com.baidu.shop.util.StringUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName ShopElasticsearchServiceImpl
 * @Description: TODO
 * @Author lite
 * @Date 2020/9/16
 * @Version V1.0
 **/
@RestController
public class ShopElasticsearchServiceImpl extends BaseApiService implements ShopElasticsearchService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private SpecificationFeign specificationFeign;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public Result<List<GoodsDoc>> search(String search) {

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        if(StringUtil.isNotEmpty(search)){
            //分页
            queryBuilder.withPageable(PageRequest.of(0,10));
            //多个字段同时查询
            queryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","categoryName"));
        }
        //设置高亮字段
        queryBuilder.withHighlightBuilder(ESHighLightUtil.getHighlightBuilder("title"));

        //将条件放进去查询
        SearchHits<GoodsDoc> hits = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsDoc.class);

        //将返回内容替换高亮
        List<SearchHit<GoodsDoc>> highLightHit = ESHighLightUtil.getHighLightHit(hits.getSearchHits());
        
        List<GoodsDoc> goodsDocs = highLightHit.stream().map(searchHit -> { return searchHit.getContent();}).collect(Collectors.toList());


        return this.setResultSuccess(goodsDocs);
    }

    @Override
    public Result<JSONObject> initEsData() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if(!indexOperations.exists()){
            indexOperations.create();
            System.out.println("创建索引成功");
            indexOperations.createMapping();
            System.out.println("创建映射成功");
        }

        //批量新增数据
        List<GoodsDoc> goodsDocs = this.esGoodsInfo();
        elasticsearchRestTemplate.save(goodsDocs);
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> clearEsData() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if (indexOperations.exists()) {
            indexOperations.delete();
            System.out.println("索引删除成功");
        }
        return this.setResultSuccess();
    }

    private List<GoodsDoc> esGoodsInfo() {

        SpuDTO spuDTO = new SpuDTO();

//        spuDTO.setPage(1);
//        spuDTO.setRows(5);`

        Result<List<SpuDTO>> spuInfo = goodsFeign.getSpuInfo(spuDTO);

        //用来装goodsDoc对象的集合
        List<GoodsDoc> goodsDocList = new ArrayList<>();

        if(spuInfo.getCode() == HTTPStatus.OK){
            //spu数据
            List<SpuDTO> spuDTOList = spuInfo.getData();

            spuDTOList.stream().forEach(spu -> {

                GoodsDoc goodsDoc = new GoodsDoc();
                //spu数据填充
                goodsDoc.setId(spu.getId().longValue());
                goodsDoc.setBrandId(spu.getBrandId().longValue());
                goodsDoc.setBrandName(spu.getBrandName());
                goodsDoc.setCategoryName(spu.getCategoryName());
                goodsDoc.setCid1(spu.getCid1().longValue());
                goodsDoc.setCid2(spu.getCid2().longValue());
                goodsDoc.setCid3(spu.getCid3().longValue());
                goodsDoc.setCreateTime(spu.getCreateTime());
                goodsDoc.setTitle(spu.getTitle());
                goodsDoc.setSutTitle(spu.getSubTitle());


                Map<List<Long>, List<Map<String, Object>>> skus = this.getSkusAndPriceList(spu.getId());
                skus.forEach((key,value) ->{
                    goodsDoc.setPrice(key);
                    goodsDoc.setSkus(JSONUtil.toJsonString(value));
                });

                Map<String, Object> specMap = this.getSpecMap(spu);

                goodsDoc.setSpecs(specMap);

                goodsDocList.add(goodsDoc);
            });

        }
        return goodsDocList;
    }



    private Map<List<Long>, List<Map<String, Object>>> getSkusAndPriceList(Integer spuId){
        Map<List<Long>, List<Map<String, Object>>> hashMap  = new HashMap<>();
        //通过spuId 查询skuList
        //sku数据填充
        Result<List<SkuDTO>> skuResult = goodsFeign.getSkuBySpuId(spuId);

        List<Long> priceList = new ArrayList<>();

        if (skuResult.getCode() == HTTPStatus.OK) {

            List<SkuDTO> skuDTOList = skuResult.getData();

            List<Map<String, Object>> skuListMap = skuDTOList.stream().map(sku -> {

                Map<String, Object> map = new HashMap<>();
                map.put("id", sku.getId());
                map.put("title", sku.getTitle());
                map.put("images", sku.getImages());
                map.put("price", sku.getPrice());

                priceList.add(sku.getPrice().longValue());

                return map;
            }).collect(Collectors.toList());

            hashMap.put(priceList,skuListMap);
        }

        return hashMap;
    }

    private  Map<String,Object> getSpecMap(SpuDTO spuDTO){
        //规格数据填充
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(spuDTO.getCid3());
        //通过分类id查询规格参数
        Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParamInfo(specParamDTO);

        HashMap<String, Object> specMap = new HashMap<>();

        if (specParamResult.getCode() == HTTPStatus.OK) {
            //只有规格参数id,规格参数的名字
            List<SpecParamEntity> specParamList = specParamResult.getData();
            //通过spuId 查询spuDetali,spuDetali里面有通用和特殊规格参数的值
            Result<SpuDetailEntity> spuDetailResult = goodsFeign.getSpuDetailById(spuDTO.getId());
            if (spuDetailResult.getCode() == HTTPStatus.OK) {

                SpuDetailEntity spuDetailEntity = spuDetailResult.getData();

                //将通用规格参数值转换成map
                String genericSpec = spuDetailEntity.getGenericSpec();
                Map<String, String> genericSpecMap = JSONUtil.toMapValueString(genericSpec);

                //特有规格参数值转换成map
                String specialSpec = spuDetailEntity.getSpecialSpec();
                Map<String, List<String>> specialSpecMap = JSONUtil.toMapValueStrList(specialSpec);

                specParamList.stream().forEach(param -> {
                    //判断是不是通用规格
                    if (param.getGeneric()) {
                        //判断是不是数字类型的参数 同时 判断用不用搜索过滤
                        if (param.getNumeric() && param.getSearching()) {
                            //将区间值转换成固定值
                            specMap.put(param.getName(), this.chooseSegment(genericSpecMap.get(param.getId() + ""), param.getSegments(), param.getUnit()));
                        } else {
                            specMap.put(param.getName(), genericSpecMap.get(param.getId() + ""));
                        }
                    } else {
                        specMap.put(param.getName(), specialSpecMap.get(param.getId() + ""));
                    }
                });
            }
        }
        return specMap;
    }

    /**
     * 把具体的值转换成区间-->不做范围查询
     * @param value
     * @param segments
     * @param unit
     * @return
     */
    private String chooseSegment(String value, String segments, String unit) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : segments.split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + unit + "以上";
                }else if(begin == 0){
                    result = segs[1] + unit + "以下";
                }else{
                    result = segment + unit;
                }
                break;
            }
        }
        return result;
    }
}
