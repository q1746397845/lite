package com.baidu.shop.dto;

import lombok.Data;

/**
 * @ClassName BrowsingDTO
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/28
 * @Version V1.0
 **/
@Data
public class BrowsingDTO {

    private Integer spuId;

    private String images;

    private String title;

    private Long price;
}
