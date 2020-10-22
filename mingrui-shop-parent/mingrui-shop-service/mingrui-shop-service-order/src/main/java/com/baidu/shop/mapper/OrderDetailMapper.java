package com.baidu.shop.mapper;

import com.baidu.shop.entity.OrderDetailEntity;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

/**
 * @ClassName OrderDetailMapper
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/21
 * @Version V1.0
 **/
public interface OrderDetailMapper extends InsertListMapper<OrderDetailEntity>, Mapper<OrderDetailEntity> {
}
