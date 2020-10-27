package com.baidu.shop.mapper;

import com.baidu.shop.entity.StockEntity;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface StockMapper extends Mapper<StockEntity>, DeleteByIdListMapper<StockEntity,Long> {

    @Update("update tb_stock stock set stock = stock - #{num} where sku_id = #{skuId}")
    void deleteGoodsStock(Integer num,Long skuId);

    @Update("update tb_stock stock set stock = stock + #{num} where sku_id = #{skuId}")
    void addGoodsStock(Integer num,Long skuId);
}
