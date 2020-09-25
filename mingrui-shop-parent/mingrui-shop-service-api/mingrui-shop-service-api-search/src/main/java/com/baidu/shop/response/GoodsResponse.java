package com.baidu.shop.response;

import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.status.HTTPStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @ClassName GoodsResponse
 * @Description: TODO
 * @Author lite
 * @Date 2020/9/21
 * @Version V1.0
 **/
@Data
@NoArgsConstructor
public class GoodsResponse extends Result<List<GoodsDoc>> {

    private Integer total;

    private Integer totalPage;

    private List<BrandEntity> brandList;

    private List<CategoryEntity> categoryList;

    private Map<String,List<String>> specAggInfo;


    public GoodsResponse(Integer total, Integer totalPage, List<BrandEntity> brandList, List<CategoryEntity> categoryList, List<GoodsDoc> goodsDocs,Map<String,List<String>> specAggInfo) {

        super(HTTPStatus.OK, HTTPStatus.OK + "", goodsDocs);

        this.specAggInfo = specAggInfo;
        this.total = total;
        this.totalPage = totalPage;
        this.brandList = brandList;
        this.categoryList = categoryList;
    }
}