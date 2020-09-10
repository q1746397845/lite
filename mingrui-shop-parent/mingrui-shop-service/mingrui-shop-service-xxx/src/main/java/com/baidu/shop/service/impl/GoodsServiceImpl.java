package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.*;
import com.baidu.shop.mapper.*;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.util.BaiduBeanUtil;
import com.baidu.shop.util.ObjectUtil;
import com.baidu.shop.util.StringUtil;
import com.github.pagehelper.IPage;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName GoodsService
 * @Description: TODO
 * @Author lite
 * @Date 2020/9/7
 * @Version V1.0
 **/
@RestController
public class GoodsServiceImpl extends BaseApiService implements GoodsService {

    @Resource
    private SpuMapper spuMapper;

    @Resource
    private BrandServiceImpl brandService;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private SkuMapper skuMapper;

    @Resource
    private StockMapper stockMapper;

    @Resource
    private SpuDetailMapper spuDetailMapper;


    //@Override
    public Result<PageInfo<SpuDTO>> getSpuInfos(SpuDTO spuDTO) {

        //分页
        if(ObjectUtil.isNotNull(spuDTO.getPage()) && ObjectUtil.isNotNull(spuDTO.getRows())){
            PageHelper.startPage(spuDTO.getPage(),spuDTO.getRows());
        }

        //构建条件查询
        Example example = new Example(SpuEntity.class);
        Example.Criteria criteria = example.createCriteria();

        if(ObjectUtil.isNotNull(spuDTO.getSaleable()) && spuDTO.getSaleable() != 2){
            criteria.andEqualTo("saleable",spuDTO.getSaleable());
        }
        if(StringUtil.isNotEmpty(spuDTO.getTitle())){
            criteria.andLike("title","%" + spuDTO.getTitle() + "%");
        }

        //排序
        if(StringUtil.isNotEmpty(spuDTO.getSort())) example.setOrderByClause(spuDTO.getOrderByClause());

        List<SpuEntity> list = spuMapper.selectByExample(example);



        //查询品牌名称
//        List<SpuDTO> spuDtoList  = list.stream().map(spuEntity -> {
//            //转换实体类
//            SpuDTO spuDTO1 = BaiduBeanUtil.copyProperties(spuEntity, SpuDTO.class);
//
//            //通过品牌id查询品牌名称
//            BrandDTO brandDTO = new BrandDTO();
//            brandDTO.setId(spuEntity.getBrandId());
//            Result<PageInfo<BrandEntity>> brandInfo = brandService.getBrand(brandDTO);
//            if (ObjectUtil.isNotNull(brandInfo)) {
//                List<BrandEntity> list1 = brandInfo.getData().getList();
//                if (!list1.isEmpty() && list1.size() == 1) {
//                    spuDTO1.setBrandName(list1.get(0).getName());
//                }
//            }
//
//
//            //通过品牌id查询品牌名称
//           List<BrandEntity> brnadList = brandMapper.getBrnadNameByBrandId(spuEntity.getBrandId());
//           if (!brnadList.isEmpty() && brnadList.size() == 1) spuDTO1.setBrandName(brnadList.get(0).getName());
//
//            //查询商品分类
//            //selectByIdList:当你这个参数是个list集合,这个list集里面又存储了任意个id, 这个方法会自动的遍历集合 ,根据每个id进行查询封装,返回一个list
//            //将查到的商品分类name用 / 拼接在一起
//            List<CategoryEntity> categoryEntityList = categoryMapper.selectByIdList(Arrays.asList(spuEntity.getCid1(), spuEntity.getCid2(), spuEntity.getCid3()));
//
//            String caterogyName  = categoryEntityList.stream().map(category -> {
//                return category.getName();
//            }).collect(Collectors.joining("/"));
//
//
//            spuDTO1.setCategoryName(caterogyName);
//
//            return spuDTO1;
//
//        }).collect(Collectors.toList());


        //代码简化
        List<SpuDTO> spuDtoList = list.stream().map(spuEntity -> {
            List<SpuDTO> spuList = spuMapper.getSpuDTOBySpuId(spuEntity.getId());
            return spuList.get(0);
        }).collect(Collectors.toList());

        List<Integer> listId = list.stream().map(spuEntity -> spuEntity.getId()).collect(Collectors.toList());



//        String idArr = "";
//        for (SpuEntity spuEntity : list) {
//            idArr += spuEntity.getId() + ",";
//        }
//        String ids = idArr.substring(0, idArr.length() - 1);
//
//        List<SpuDTO> spuDtoList = spuMapper.getSpuDTOBySpuIds(ids);


        PageInfo<SpuEntity> pageInfo = new PageInfo<>(list);

        //要返回的是dto的数据,dto里边每次只有5条数据,总条数要从pageInfo里边获取
        long total = pageInfo.getTotal();
        //借用一下message属性
        return this.setResult(HTTPStatus.OK,"" + total, spuDtoList);
    }

    //代码简化
    //通过一条sql语句直接干上边的事
    @Override
    public Result<List<JSONObject>> getSpuInfo(SpuDTO spuDTO) {

        //处理下分页信息,前台每次传来的 1,5  2,5  3,5............,所有得处理下
        if(ObjectUtil.isNotNull(spuDTO.getPage()) && ObjectUtil.isNotNull(spuDTO.getRows())){
            spuDTO.setPage((spuDTO.getPage()-1) * spuDTO.getRows());
        }



        List<SpuDTO> listDTO = spuMapper.getSpuDTO(spuDTO);

        Integer total = spuMapper.count(spuDTO);

        return this.setResult(HTTPStatus.OK,"" + total, listDTO);

    }


    //新增商品
    @Override
    @Transactional
    public Result<List<JSONObject>> saveGoods(SpuDTO spuDTO) {

        //新增spu
        //新增返回主键
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);

        final Date date = new Date();//保持时间一致

        //设置默认值
        spuEntity.setSaleable(1);
        spuEntity.setValid(1);
        spuEntity.setCreateTime(date);
        spuEntity.setLastUpdateTime(date);
        spuMapper.insertSelective(spuEntity);

        //新增spuDetail
        SpuDetailEntity spuDetailEntity = BaiduBeanUtil.copyProperties(spuDTO.getSpuDetail(), SpuDetailEntity.class);
        spuDetailEntity.setSpuId(spuEntity.getId());
        spuDetailMapper.insertSelective(spuDetailEntity);

        //新增sku 和 stock
        this.saveSkusAndStocks(spuDTO,date,spuEntity.getId());


        return this.setResultSuccess();
    }

    //修改商品
    @Transactional
    @Override
    public Result<List<JSONObject>> editGoods(SpuDTO spuDTO) {



        //保持时间一致
        Date date = new Date();

        //修改spu
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setLastUpdateTime(date);
        spuMapper.updateByPrimaryKeySelective(spuEntity);

        //修改spuDetail
        SpuDetailEntity spuDetailEntity = BaiduBeanUtil.copyProperties(spuDTO.getSpuDetail(), SpuDetailEntity.class);
        spuDetailMapper.updateByPrimaryKeySelective(spuDetailEntity);

        //删除sku和stock
        this.deleteSkusAndStocks(spuDTO.getId());;


        //下边封装了方法
        //修改sku 和 stock
        //修改前要先删除,因为可能存在多个
        //通过skuId删除sku 和 stock
        //先通过spuId查询skuId
//        Example example = new Example(SkuEntity.class);
//        example.createCriteria().andEqualTo("spuId",spuDTO.getId());
//        List<SkuEntity> skuEntityList = skuMapper.selectByExample(example);
//
//        List<Long> skuIdList = skuEntityList.stream().map(skuEntity -> {
//            return skuEntity.getId();
//        }).collect(Collectors.toList());
//
//        //删除sku
//        skuMapper.deleteByIdList(skuIdList);
//        //删除stock
//        stockMapper.deleteByIdList(skuIdList);

        //新增sku和stock 到数据库
        this.saveSkusAndStocks(spuDTO,date,spuDTO.getId());


        return this.setResultSuccess();
    }

    //删除商品
    @Override
    public Result<List<JSONObject>> deleteGoods(Integer spuId) {
        //删除 spu
        spuMapper.deleteByPrimaryKey(spuId);
        //删除spuDetail
        spuDetailMapper.deleteByPrimaryKey(spuId);

        //删除sku和stock
        this.deleteSkusAndStocks(spuId);

        return this.setResultSuccess();
    }

    //通过spuId查询spuDetail
    @Override
    public Result<SpuDetailEntity> getSpuDetailById(Integer spuId) {

        SpuDetailEntity spuDetailEntity = spuDetailMapper.selectByPrimaryKey(spuId);

        return this.setResultSuccess(spuDetailEntity);
    }

    //通过spuId查询sku
    @Override
    public Result<List<SkuDTO>> getSkuBySpuId(Integer spuId) {

        List<SkuDTO> skuDTO = skuMapper.selectSkuAndStockBySpuId(spuId);

        return this.setResultSuccess(skuDTO);
    }





    //新增sku和stock方法
    @Transactional
    void saveSkusAndStocks(SpuDTO spuDTO,Date date,Integer spuId){
        spuDTO.getSkus().stream().forEach(skuDto ->{
            SkuEntity skuEntity = BaiduBeanUtil.copyProperties(skuDto, SkuEntity.class);
            skuEntity.setCreateTime(date);
            skuEntity.setLastUpdateTime(date);
            skuEntity.setSpuId(spuId);
            skuMapper.insertSelective(skuEntity);

            StockEntity stockEntity = new StockEntity();
            stockEntity.setStock(skuDto.getStock());
            stockEntity.setSkuId(skuEntity.getId());
            stockMapper.insertSelective(stockEntity);
        });
    }

    //删除sku和stock方法
    @Transactional
    void deleteSkusAndStocks(Integer spuId){
        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId",spuId);
        List<SkuEntity> skuEntityList = skuMapper.selectByExample(example);

        List<Long> skuIdList = skuEntityList.stream().map(skuEntity -> {
            return skuEntity.getId();
        }).collect(Collectors.toList());

        if(skuIdList.size() > 0){
            //删除sku
            skuMapper.deleteByIdList(skuIdList);
            //删除stock
            stockMapper.deleteByIdList(skuIdList);
        }
    }


    //控制商品上下架
    @Override
    @Transactional
    public Result<List<SkuDTO>> updateSaleable(SpuDTO spuDTO) {

        //只修改上下架
        if(ObjectUtil.isNotNull(spuDTO.getId())
                && ObjectUtil.isNotNull(spuDTO.getSaleable())
                && (spuDTO.getSaleable() == 0 || spuDTO.getSaleable() == 1)){

            SpuEntity spuEntity = new SpuEntity();
            spuEntity.setId(spuDTO.getId());
            spuEntity.setSaleable(spuDTO.getSaleable());

            spuMapper.updateByPrimaryKeySelective(spuEntity);
            return this.setResultSuccess();
        }

        return this.setResultError("操作失败");
    }
}
