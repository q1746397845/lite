package com.baidu.shop.mapper;

import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.special.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName CategoryBrandMapper
 * @Description: TODO
 * @Author lite
 * @Date 2020/9/1
 * @Version V1.0
 **/
public interface  CategoryBrandMapper extends Mapper<CategoryBrandEntity>, InsertListMapper<CategoryBrandEntity> {

    //通过分类id查询品牌信息
    @Select(value = "select * from tb_brand where id in(select brand_id from tb_category_brand where category_id = #{id})")
    List<BrandEntity> getBrandByCategoryId(Integer id);

    //通过品牌id查询分类信息
    @Select(value = "select c.id,c.name from tb_category c where c.id in(select cb.category_id from tb_category_brand cb where cb.brand_id = #{brandId})")
    List<CategoryEntity> getCatesByBrandId(Integer brandId);
}
