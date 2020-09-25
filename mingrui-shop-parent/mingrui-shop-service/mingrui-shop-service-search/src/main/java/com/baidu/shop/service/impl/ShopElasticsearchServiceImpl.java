package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.feign.BrandFeign;
import com.baidu.shop.feign.CategoryFeign;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecificationFeign;
import com.baidu.shop.response.GoodsResponse;
import com.baidu.shop.service.ShopElasticsearchService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.util.ESHighLightUtil;
import com.baidu.shop.util.JSONUtil;
import com.baidu.shop.util.StringUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
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

    @Autowired
    private BrandFeign brandFeign;

    @Autowired
    private CategoryFeign categoryFeign;

    /**
     * es查询
     * @param search
     * @param page
     * @return
     */
    @Override
    public GoodsResponse search(String search,Integer page,String filter) {

        if(StringUtil.isEmpty(search)) new RuntimeException("没有值");

        //构建条件
        NativeSearchQueryBuilder queryBuilder = this.getQueryBuilder(search,page,filter);

        //将条件放进去查询
        SearchHits<GoodsDoc> hits = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsDoc.class);

        //将返回内容替换高亮
        List<SearchHit<GoodsDoc>> highLightHit = ESHighLightUtil.getHighLightHit(hits.getSearchHits());

        //将要返回到页面的数据遍历出来
        List<GoodsDoc> goodsDocs = highLightHit.stream().map(searchHit -> { return searchHit.getContent();}).collect(Collectors.toList());

        //查询出总条数
        int total = Long.valueOf(hits.getTotalHits()).intValue();
        //查询总页数
        //将查询出来的值转换成 long的包装类, 再转换成double类型,/10向上取整 算出总页数,再将值转换成double类型的包装类,再将包装类转换成int类型
        int totalPage = Double.valueOf(Math.ceil(Long.valueOf(hits.getTotalHits()).doubleValue() / 10)).intValue();

        //获取聚合数据
        Aggregations aggregations = hits.getAggregations();
        //获取品牌集合
        List<BrandEntity> brandList = this.getBrandList(aggregations);
        //获取分类数据集合
        //List<CategoryEntity> categoryList = this.getCategoryList(aggregations);

        //所有分类的数据集合
        List<CategoryEntity> categoryList = null;
        //热度最高的分类id
        Integer hotCid = 0;

        //key是热度最高的分类id  value是要返回到页面的数据
        Map<Integer, List<CategoryEntity>> map = this.getCategoryList(aggregations);

        for(Map.Entry<Integer, List<CategoryEntity>> entry:map.entrySet()){
            categoryList = entry.getValue();
            hotCid = entry.getKey();
        }

        //通过分类id 查询 规格参数
        //specAggInfo 里的key是 规格参数的name ,value是具体的规格参数
        Map<String, List<String>> specAggInfo = this.getSpecAggInfo(hotCid, search);

        GoodsResponse goodsResponse = new GoodsResponse(total,totalPage,brandList,categoryList,goodsDocs,specAggInfo);

        return goodsResponse;
    }


    /**
     * 通过 分组id 和 前台传来的查询参数 得到 参数name和具体的参数值并放到map中返回
     * @param hotCid
     * @param search
     * @return
     */
    private Map<String, List<String>>  getSpecAggInfo(Integer hotCid,String search){
        //通过 分类id 和 是否用于搜索属性, 查询分类可用于搜索的规格参数
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(hotCid);
        specParamDTO.setSearching(true);
        Result<List<SpecParamEntity>> specParamInfo = specificationFeign.getSpecParamInfo(specParamDTO);
        if(specParamInfo.getCode() == 200){
            List<SpecParamEntity> paramList = specParamInfo.getData();

            //多个字段同时查询
            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
            queryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","categoryName"));

            //分页让它查询一条数据
            queryBuilder.withPageable(PageRequest.of(0,1));

            //聚合查询
            paramList.stream().forEach(param ->{
                //通过规格参数name聚合
                queryBuilder.addAggregation(AggregationBuilders.terms(param.getName()).field("specs." + param.getName() + ".keyword"));
            });

            SearchHits<GoodsDoc> searchHits = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsDoc.class);

            Aggregations aggregations = searchHits.getAggregations();

            Map<String, List<String>> map = new HashMap<>();


            //通过 规格参数name 分别获取到 聚合数据
            paramList.stream().forEach(param ->{

                //从聚合数据中获得桶
                Terms terms = aggregations.get(param.getName());
                List<? extends Terms.Bucket> buckets = terms.getBuckets();

                //从 桶中 获取到聚合后得到的值
                List<String> value = buckets.stream().map(bucket -> {
                    return bucket.getKeyAsString();

                }).collect(Collectors.toList());

                //将 规格参数name 和 规格参数值 放到map中
                //value是通过 规格参数name 聚合 查询后得到的 规格参数值
                map.put(terms.getName(),value);
            });
            return map;
        }
    return null;
    }

    /**
     * 构建es查询条件
     * @param search
     * @param page
     * @return
     */
    private NativeSearchQueryBuilder getQueryBuilder(String search,Integer page,String filter){
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //过滤
        if(filter.length() > 2 && StringUtil.isNotEmpty(filter)){

            //构建布尔查询
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            //将传过来的json字符串转换为map对象
            Map<String, String> filterMap = JSONUtil.toMapValueString(filter);

            filterMap.forEach((key,value) ->{
                MatchQueryBuilder matchQueryBuilder = null;

                //单字段查询
                if(key.equals("cid3") || key.equals("brandId")){
                    matchQueryBuilder = QueryBuilders.matchQuery(key,value);
                }else{
                    matchQueryBuilder = QueryBuilders.matchQuery("specs." +key + ".keyword",value);
                }

                //must（与）、must_not（非）、should（或)
                boolQueryBuilder.must(matchQueryBuilder);
            });
            //将过滤条件放到查询里
            queryBuilder.withFilter(boolQueryBuilder);
        }


        //多个字段同时查询
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(search, "title", "brandName", "categoryName");

        //将查询条件放到查询里
        queryBuilder.withQuery(multiMatchQueryBuilder);

        //分页
        queryBuilder.withPageable(PageRequest.of(page-1,10));
        //设置高亮字段
        queryBuilder.withHighlightBuilder(ESHighLightUtil.getHighlightBuilder("title"));

        //聚合
        queryBuilder.addAggregation(AggregationBuilders.terms("brand_agg").field("brandId"));
        queryBuilder.addAggregation(AggregationBuilders.terms("category_agg").field("cid3"));


        return queryBuilder;
    }


    /**
     * 获取分类集合和热度最高的分类id
     * @param aggregations
     * @return
     */
    private Map<Integer, List<CategoryEntity>> getCategoryList(Aggregations aggregations){
        Terms category_agg = aggregations.get("category_agg");

        List<? extends Terms.Bucket> categoryBuckets = category_agg.getBuckets();

        Map<Integer, List<CategoryEntity>> map = new HashMap<>();

        //热度最高的分类id
        List<Integer> hotCidList = Arrays.asList(0);

        List<Long> maxCountList = Arrays.asList(0L);

        List<String> categoryIdList = categoryBuckets.stream().map(categoryBucket -> {

            Number keyAsNumber = categoryBucket.getKeyAsNumber();

            //获得到通过聚合后热度最高的分类id
            if(categoryBucket.getDocCount() > maxCountList.get(0)){
                hotCidList.set(0,keyAsNumber.intValue());
                maxCountList.set(0,categoryBucket.getDocCount());
            }
            return keyAsNumber + "";
        }).collect(Collectors.toList());

        //通过分类id获取分类详细数据
        Result<List<CategoryEntity>> categoryResult = categoryFeign.getCategoryByCategoryIds(String.join(",", categoryIdList));

        //key的值是热度最高的cid ,value是cid集合对应的数据
        map.put(hotCidList.get(0),categoryResult.getData());

        return map;
    }

    /**
     * 获取品牌集合
     * @param aggregations
     * @return
     */
    private List<BrandEntity> getBrandList(Aggregations aggregations){

        Terms brand_agg = aggregations.get("brand_agg");

        List<? extends Terms.Bucket> brandBuckets = brand_agg.getBuckets();

        List<String> brandIdList = brandBuckets.stream().map(brandBucket -> brandBucket.getKeyAsString()).collect(Collectors.toList());

        //通过brandId获取brand详细数据
        //String.join(分隔符,List<String>),将list集合转为,分隔的字符串
        Result<List<BrandEntity>> brandResult = brandFeign.getBrandByBrandIds(String.join(",", brandIdList));
        return brandResult.getData();
    }

    /**
     * 初始化es数据
     * @return
     */
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

    /**
     * 删除es索引
     * @return
     */
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
