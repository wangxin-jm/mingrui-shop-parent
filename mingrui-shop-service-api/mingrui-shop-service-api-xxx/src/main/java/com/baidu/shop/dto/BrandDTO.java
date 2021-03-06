package com.baidu.shop.dto;

import com.baidu.shop.base.BaseDTO;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 2 * @ClassName BrandDTO
 * 3 * @Description: TODO
 * 4 * @Author wangxin
 * 5 * @Date 2020/12/25
 * 6 * @Version V1.0
 * 7
 **/
@ApiModel(value="品牌DTO")
@Data
public class BrandDTO extends BaseDTO {

    @ApiModelProperty(value = "品牌主键",example = "1")
    @NotNull(message = "主键不能为空", groups = {MingruiOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "品牌名称")
    @NotEmpty(message = "名牌名称不能为空", groups ={MingruiOperation.Add.class,MingruiOperation.Update.class})
    private String name;

    @ApiModelProperty(value = "品牌图片")
    private String image;

    @ApiModelProperty(value = "品牌首字母")
    @NotNull(message = "品牌首字母不能为空", groups ={MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Character letter;

    @ApiModelProperty(value = "品牌id集合")
    @NotNull(message = "品牌id集合不能为空", groups ={MingruiOperation.Add.class,MingruiOperation.Update.class})
    private String categories;

}

