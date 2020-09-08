package com.baidu.shop.mapper;

import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.SpecGroupEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CategoryMapper extends Mapper<CategoryEntity> , SelectByIdListMapper<CategoryEntity,Integer> {

    @Select(value = "select c.id,c.name from tb_category c where c.id in(select cb.category_id from tb_category_brand cb where cb.brand_id = #{brandId})")
    List<CategoryEntity> getCatesByBrand(Integer brandId);

    @Select(value = "select * from tb_spec_group where cid = #{id}")
    List<SpecGroupEntity> getGroupByCategoryId(Integer id);

    @Select(value = "select name from tb_brand where id in(select brand_id from tb_category_brand where category_id = #{id})")
    List<BrandEntity> getBrandByCategoryId(Integer id);

}
