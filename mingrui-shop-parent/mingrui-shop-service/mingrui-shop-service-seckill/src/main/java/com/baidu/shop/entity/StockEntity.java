package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName StockEntity
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/31
 * @Version V1.0
 **/
@Table(name = "stock")
@Data
public class StockEntity {

    @Id
    private Integer id;

    private String name;//名称

    private Integer count;//库存

    private Integer sale;//已售

    private Integer version;//乐观锁,版本号
}
