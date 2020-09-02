package com.baidu.shop.mapper;

import com.baidu.shop.entity.CategoryBrandEntity;
import tk.mybatis.mapper.common.special.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @ClassName CategoryBrandMapper
 * @Description: TODO
 * @Author lite
 * @Date 2020/9/1
 * @Version V1.0
 **/
public interface  CategoryBrandMapper extends Mapper<CategoryBrandEntity>, InsertListMapper<CategoryBrandEntity> {
}
