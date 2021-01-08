package com.baidu.shop.dto;

import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 2 * @ClassName SkuDTO
 * 3 * @Description: TODO
 * 4 * @Author wangxin
 * 5 * @Date 2021/1/7
 * 6 * @Version V1.0
 * 7
 **/
@ApiModel(value="SKU的属性传输类")
@Data
public class SkuDTO {

    @ApiModelProperty(value = "主键",example = "1")
    @NotNull(message = "Id不能为空",groups = {MingruiOperation.Update.class})
    private Long id;

    @ApiModelProperty(value = "spu主键",example = "1")
    @NotNull(message = "spuId不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Integer spuId;

    @ApiModelProperty("商品标题")
    @NotEmpty(message = "商品名字不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private String title;

    @ApiModelProperty(value = "商品图片,多个图片用,好分割")
    private String images;

    @ApiModelProperty(value="商品价格,单位为分",example = "1")
    @NotNull(message = "价格不能为空", groups={MingruiOperation.Update.class,MingruiOperation.Add.class})
    private Integer price;

    @ApiModelProperty(value = "特有规格属性在spu属性模板中对应下表的组合")
    private String indexes;

    @ApiModelProperty(value="sku的特有规格参数键值对,json格式")
    private String ownSpec;

    @ApiModelProperty(value = "是否有效 0无效 1有效",example = "1")
    @NotNull(message = "是否有效不能为空", groups={MingruiOperation.Update.class,MingruiOperation.Add.class})
    private Boolean enable;

    @ApiModelProperty(value="创建时间")
    @NotNull(message = "创建时间不能为空", groups={MingruiOperation.Update.class,MingruiOperation.Add.class})
    private Date createTime;

    @ApiModelProperty(value="最后修改时间")
    @NotNull(message = "最后修改时间不能为空", groups={MingruiOperation.Update.class,MingruiOperation.Add.class})
    private Date lastUpdateTime;

    @ApiModelProperty(value="库存")
    @NotNull(message = "价格不能为空", groups={MingruiOperation.Update.class,MingruiOperation.Add.class})
    private Integer stock;

}
