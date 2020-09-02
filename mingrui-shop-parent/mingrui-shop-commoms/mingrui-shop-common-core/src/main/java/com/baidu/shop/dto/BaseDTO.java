package com.baidu.shop.dto;

import com.baidu.shop.util.StringUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName BaseDTO
 * @Description: TODO
 * @Author lite
 * @Date 2020/8/31
 * @Version V1.0
 **/
@Data
@ApiModel(value = "BaseDTO用于数据传输,其他dto需要继承此类")
public class BaseDTO {

    @ApiModelProperty(value = "当前页",example = "1")
    private Integer page;

    @ApiModelProperty(value = "每页显示多少条数据",example = "1")
    private Integer rows;

    @ApiModelProperty(value = "排序字段")
    private String sort;

    @ApiModelProperty(value = "是否降序")
    private Boolean order;

    @ApiModelProperty(hidden = true)
    public String getOrderByClause(){
        if(StringUtil.isNotEmpty(sort)){
            return sort + " " +(order?"desc":"");
        }

        return null;
    }
}
