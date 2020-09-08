package com.baidu.shop.mapper;

import com.baidu.shop.entity.BrandEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName BrandMapper
 * @Description: TODO
 * @Author lite
 * @Date 2020/8/31
 * @Version V1.0
 **/
public interface BrandMapper extends Mapper<BrandEntity> {

    @Select(value = "select name from tb_brand where id = #{brandId}")
    List<BrandEntity> getBrnadNameByBrandId(Integer brandId);
}
