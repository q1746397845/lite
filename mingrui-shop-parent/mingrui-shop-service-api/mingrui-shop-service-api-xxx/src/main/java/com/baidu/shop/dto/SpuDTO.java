package com.baidu.shop.dto;

import com.baidu.shop.validate.group.MingruiOperation;
import com.sun.rowset.internal.BaseRow;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @ClassName SpuDTO
 * @Description: TODO
 * @Author lite
 * @Date 2020/9/7
 * @Version V1.0
 **/
@Data
@ApiModel(value = "spu数据传输DTO")
public class SpuDTO extends BaseDTO {

    @ApiModelProperty(value = "主键",example = "1")
    @NotNull(message = "主键不能为空",groups = {MingruiOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "标题")
    @NotEmpty(message = "标题不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private String title;

    @ApiModelProperty(value = "子标题")
    private String subTitle;

    @ApiModelProperty(value = "1级类目id",example = "1")
    @NotNull(message = "1级类目id不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Integer cid1;

    @ApiModelProperty(value = "2级类目id",example = "1")
    @NotNull(message = "2级类目id不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Integer cid2;

    @ApiModelProperty(value = "3级类目id",example = "1")
    @NotNull(message = "3级类目id不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Integer cid3;

    @ApiModelProperty(value = "商品所属品牌id",example = "1")
    @NotNull(message = "商品所属品牌id不能为空",groups = {MingruiOperation.Add.class})
    private Integer BrandId;

    @ApiModelProperty(value = "是否上架,0下架,1上架",example = "1")
    private Integer saleable;

    @ApiModelProperty(value = "是否有效,0已删除,1有效",example = "1")
    private Integer valid;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "最后修改时间")
    private Date lastUpdateTime;

    private String brandName;

    private String categoryName;


}
