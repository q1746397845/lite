package com.baidu.shop.mapper;

import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.entity.SpecParamEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName SpecGroupMapper
 * @Description: TODO
 * @Author lite
 * @Date 2020/9/3
 * @Version V1.0
 **/
public interface SpecGroupMapper extends Mapper<SpecGroupEntity> {



    @Select(value = "select * from tb_spec_group where cid = #{id}")
    List<SpecGroupEntity> getGroupByCategoryId(Integer id);

}
