package com.baidu.shop.dto;

import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 2 * @ClassName StockDTO
 * 3 * @Description: TODO
 * 4 * @Author wangxin
 * 5 * @Date 2021/1/7
 * 6 * @Version V1.0
 * 7
 **/
@ApiModel(value = "库存数据传输类")
@Data
public class StockDTO {
    @ApiModelProperty(value = "sku主键", example = "1")
    @NotNull(message = "库存对应的商品sku id不能为空", groups={MingruiOperation.Update.class,MingruiOperation.Add.class})
    private Long skuId;

    @ApiModelProperty(value = "可秒杀库存", example = "1")
    private Integer seckillStock;

    @ApiModelProperty(value = "秒杀总数量", example = "1")
    private Integer seckillTotal;

    @ApiModelProperty(value = "库存数量", example = "1")
    @NotNull(message = "库存对应的商品sku id不能为空", groups={MingruiOperation.Update.class,MingruiOperation.Add.class})
    private Integer stock;
}
