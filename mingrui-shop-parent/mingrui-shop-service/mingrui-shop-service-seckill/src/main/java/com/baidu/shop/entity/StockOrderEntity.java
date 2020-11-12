package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @ClassName StockOrder
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/31
 * @Version V1.0
 **/
@Data
@Table(name = "stock_order")
public class StockOrderEntity {

    @Id
    private Integer id;

    private Integer sid;

    private String name;

    private Date CreateTime;
}
