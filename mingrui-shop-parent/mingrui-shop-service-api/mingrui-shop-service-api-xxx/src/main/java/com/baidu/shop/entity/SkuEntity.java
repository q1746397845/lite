package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Table;
import java.util.Date;

/**
 * @ClassName SkuEntity
 * @Description: TODO
 * @Author lite
 * @Date 2020/9/8
 * @Version V1.0
 **/
@Data
@Table(name = "tb_sku")
public class SkuEntity {

    private Long id;

    private Integer spuId;

    private String title;

    private String images;

    private Integer price;

    private String indexes;

    private String ownSpec;

    private Integer  enable;

    private Date createTime;

    private Date lastUpdateTime;
}
