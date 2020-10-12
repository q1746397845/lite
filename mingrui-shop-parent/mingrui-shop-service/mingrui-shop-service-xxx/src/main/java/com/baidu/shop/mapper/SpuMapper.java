package com.baidu.shop.mapper;

import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpuEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpuMapper extends Mapper<SpuEntity> {

    @Select(value = "SELECT s.*,b.name as brandName,GROUP_CONCAT(c.name separator '/')as categoryName from tb_category c,tb_brand b,tb_spu s \n" +
            "where c.id in(s.cid1,s.cid2,s.cid3) and b.id = s.brand_id and s.id = #{id}")
    List<SpuDTO> getSpuDTOBySpuId(Integer id);


    @Select(value = "SELECT s.*,b.name as brandName,GROUP_CONCAT(c.name separator '/')as categoryName from tb_category c,tb_brand b,tb_spu s where c.id in(s.cid1,s.cid2,s.cid3) and b.id = s.brand_id and s.id in (#{ids}) group by s.id")
    List<SpuDTO> getSpuDTOBySpuIds(String ids);


    List<SpuDTO> getSpuDTO(SpuDTO spuDTO);

    Integer count(SpuDTO spuDTO);
}
