package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.entity.SpuEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.mapper.SpuMapper;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.util.BaiduBeanUtil;
import com.baidu.shop.util.ObjectUtil;
import com.baidu.shop.util.PinyinUtil;
import com.baidu.shop.util.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import com.baidu.shop.base.BaseApiService;
import tk.mybatis.mapper.entity.Example;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName BrandServiceImpl
 * @Description: TODO
 * @Author lite
 * @Date 2020/8/31
 * @Version V1.0
 **/
@RestController
public class BrandServiceImpl  extends BaseApiService implements BrandService {

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private CategoryBrandMapper categoryBrandMapper;

    @Resource
    private SpuMapper spuMapper;


    @Override
    public Result<PageInfo<BrandEntity>> getBrand(BrandDTO brandDTO) {

        //分页
        if(ObjectUtil.isNotNull(brandDTO.getPage()) && ObjectUtil.isNotNull(brandDTO.getRows())){
            PageHelper.startPage(brandDTO.getPage(),brandDTO.getRows());
        }

        Example example = new Example(BrandEntity.class);
        Example.Criteria criteria = example.createCriteria();

        //条件查询
        if(StringUtil.isNotEmpty(brandDTO.getName())){
            criteria.andLike("name","%" + brandDTO.getName() + "%");
        }
        if(ObjectUtil.isNotNull((brandDTO.getId()))){
            criteria.andEqualTo("id",brandDTO.getId());
        }

        //给排序字段排序
        if(StringUtil.isNotEmpty(brandDTO.getSort())){example.setOrderByClause(brandDTO.getOrderByClause());}

        List<BrandEntity> list = brandMapper.selectByExample(example);

        //将查询数据放到页面信息中
        PageInfo<BrandEntity> pageInfo = new PageInfo<>(list);

        //将数据返回
        return this.setResultSuccess(pageInfo);
    }

    @Transactional
    @Override
    public Result<JSONObject> saveBrand(BrandDTO brandDTO) {


        //获取品牌名称的首字母
        //获得品牌名称
//        String name = brandDTO.getName();
//        //将第一个字符转换为String类型字符串
//        String s = String.valueOf(name.charAt(0));
//        //调用工具类,将字符串转换为拼音,并转换为大写
//        String upperCase = PinyinUtil.getUpperCase(s, PinyinUtil.TO_FIRST_CHAR_PINYIN);
//        //获得拼音的首字母放到实体类里边
//        brandDTO.setLetter(upperCase.charAt(0));
        //简化后的代码
        brandDTO.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandDTO.getName().charAt(0)), PinyinUtil.TO_FIRST_CHAR_PINYIN).charAt(0));

        //转换实体类
        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);

        //新增品牌数据,通用mapper新增返回主键
        brandMapper.insertSelective(brandEntity);


       this.saveCateBrand(brandDTO,brandEntity);

        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> editBrand(BrandDTO brandDTO) {

        //获取品牌名称的首字母
        //获得品牌名称
//        String name = brandDTO.getName();
//        //将第一个字符转换为String类型字符串
//        String s = String.valueOf(name.charAt(0));
//        //调用工具类,将字符串转换为拼音,并转换为大写
//        String upperCase = PinyinUtil.getUpperCase(s, PinyinUtil.TO_FIRST_CHAR_PINYIN);
//        //获得拼音的首字母放到实体类里边
//        brandDTO.setLetter(upperCase.charAt(0));

        //获取品牌名称的首字母,简化后的代码
        brandDTO.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandDTO.getName().charAt(0)), PinyinUtil.TO_FIRST_CHAR_PINYIN).charAt(0));

        //转换实体类
        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);
        brandMapper.updateByPrimaryKeySelective(brandEntity);

        if(StringUtil.isNotEmpty(brandDTO.getCategory())){
            Example example = new Example(CategoryBrandEntity.class);
            example.createCriteria().andEqualTo("brandId",brandEntity.getId());
            categoryBrandMapper.deleteByExample(example);

            this.saveCateBrand(brandDTO,brandEntity);
        }

        return this.setResultSuccess();
    }



    //新增到关系表方法
    @Transactional
    void saveCateBrand(BrandDTO brandDTO,BrandEntity brandEntity){

        //分类品牌关系表新增数据
        //判断新增的分类信息是一个还是多个
        if(brandDTO.getCategory().contains(",")){
            //批量新增
            //将 要新增的分类信息 通过逗号分割,获得String类型数组
//            String[] cateIdArr = brandDTO.getCategory().split(",");
//            //将String类型数组转换为list集合
//            List<String> list = Arrays.asList(cateIdArr);
//            //遍历list集合  .map有返回值  .forEach没有返回值
//            //返回一个list类型集合
//            List<CategoryBrandEntity> collect = list.stream().map(cateId -> {
//                CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
//                categoryBrandEntity.setCategoryId(StringUtil.toInteger(cateId));
//                categoryBrandEntity.setBrandId(brandEntity.getId());
//                return categoryBrandEntity;
//            }).collect(Collectors.toList());
//            //批量新增到关系表中
//            categoryBrandMapper.insertList(collect);


            //代码简化
            categoryBrandMapper.insertList(Arrays.asList(brandDTO.getCategory().split(",")).stream().map(cateId ->{
                CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
                categoryBrandEntity.setCategoryId(StringUtil.toInteger(cateId));
                categoryBrandEntity.setBrandId(brandEntity.getId());
                return categoryBrandEntity;
            }).collect(Collectors.toList()));


        }else{
            CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
            //因为有新增返回主键,可以直接得到 brandEntity.getId()
            categoryBrandEntity.setBrandId(brandEntity.getId());
            //调用方法将String类型转换为Integer类型
            categoryBrandEntity.setCategoryId(StringUtil.toInteger(brandDTO.getCategory()));
            //新增到关系表中
            categoryBrandMapper.insertSelective(categoryBrandEntity);
        }
    }


    @Transactional
    @Override
    public Result<JSONObject> deleteBrand(Integer id) {

        //判断品牌有没有被商品绑定
        Example SpuEntityExample = new Example(SpuEntity.class);
        SpuEntityExample.createCriteria().andEqualTo("BrandId",id);
        List<SpuEntity> spuList = spuMapper.selectByExample(SpuEntityExample);
        if(spuList.size() > 0){
            StringBuilder brandName = new StringBuilder();
            spuList.stream().forEach(spu ->{
                brandName.append(" " + spu.getTitle() + " ");
            });
            return this.setResultError("当前品牌被: " + brandName + "绑定,不能被删除");
        }

        //删除品牌和关系表中的数据
        brandMapper.deleteByPrimaryKey(id);

        Example example = new Example(CategoryBrandEntity.class);
        example.createCriteria().andEqualTo("brandId",id);
        categoryBrandMapper.deleteByExample(example);

        return this.setResultSuccess();
    }




    @Override
    public Result<List<BrandEntity>> getBrandByCategoryId(Integer categoryId) {
        if(ObjectUtil.isNotNull(categoryId)){
            List<BrandEntity> list = categoryBrandMapper.getBrandByCategoryId(categoryId);
            return this.setResultSuccess(list);
        }

        return this.setResultError("没查到");
    }
}
