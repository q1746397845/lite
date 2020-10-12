package com.baidu.shop.DTO;

import lombok.Data;

/**
 * @ClassName SearchDTO
 * @Description: TODO
 * @Author lite
 * @Date 2020/9/28
 * @Version V1.0
 **/
@Data
public class SearchDTO {

    private String search;

    private Integer page;

    private String filter;
}
