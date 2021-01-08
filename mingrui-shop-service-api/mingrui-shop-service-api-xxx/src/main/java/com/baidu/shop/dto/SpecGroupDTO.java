package com.baidu.shop.dto;

import com.baidu.shop.base.BaseDTO;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 2 * @ClassName SpecifiactionDTO
 * 3 * @Description: TODO
 * 4 * @Author wangxin
 * 5 * @Date 2021/1/4
 * 6 * @Version V1.0
 * 7
 **/
@ApiModel(value = "规格租数据传输DTO")
@Data
public class SpecGroupDTO extends BaseDTO {

    @ApiModelProperty(value = "主键",example = "1")
    @NotNull(message = "主键不能为空",groups = {MingruiOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "类型Id",example = "1")
    @NotNull(message = "类型cId不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Integer cid;

    @ApiModelProperty("规格组名称")
    @NotEmpty(message = "规格组名称不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private String name;




}
