package com.baidu.shop.mapper;


import com.baidu.shop.entity.SkuEntity;
import com.baidu.shop.dto.SkuDTO;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SkuMapper extends Mapper<SkuEntity>, DeleteByIdListMapper<SkuEntity,Long> {

    @Select("select sk.*,st.stock,sk.own_spec as ownSpec from tb_stock st,tb_sku sk where sk.id = st.sku_id and sk.spu_id = #{spuId}")
    List<SkuDTO> selectSkuAndStockBySpuId(Integer spuId);
}
