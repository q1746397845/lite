package com.baidu.shop.dto;

import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.codehaus.jackson.map.Serializers;

import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @ClassName BrandDTO
 * @Description: TODO
 * @Author lite
 * @Date 2020/8/31
 * @Version V1.0
 **/
@ApiModel(value = "品牌实体类")
@Data
public class BrandDTO extends BaseDTO {

    @ApiModelProperty(value = "品牌id")
    @NotNull(message = "品牌id不能为空",groups = {MingruiOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "品牌name")
    @NotEmpty(message = "品牌name不能为空",groups = {MingruiOperation.Update.class,MingruiOperation.Add.class})
    private String name;

    @ApiModelProperty(value = "品牌logo")
    private String image;

    @ApiModelProperty(value = "品牌首字母")
    @NotNull(message = "品牌首字母不能为空")
    private Character letter;

    @ApiModelProperty(value = "品牌分类信息")
    @NotEmpty(message = "品牌分类信息不能为空",groups = {MingruiOperation.Add.class})
    private String category;
}
