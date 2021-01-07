package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 2 * @ClassName StockEntity
 * 3 * @Description: TODO
 * 4 * @Author wangxin
 * 5 * @Date 2021/1/7
 * 6 * @Version V1.0
 * 7
 **/
@Data
@Table(name="tb_stock")
public class StockEntity {

    @Id
    private Long skuId;

    private Integer seckillStock;

    private Integer seckillTotal;

    private Integer stock;


}
