package com.baidu.shop.entity;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

/**
 * 2 * @ClassName CategoryEntityBrandEntity
 * 3 * @Description: TODO
 * 4 * @Author wangxin
 * 5 * @Date 2020/12/28
 * 6 * @Version V1.0
 * 7
 **/
@Data
@Table(name="tb_category_brand")
@ApiModel(value="品牌分类关系表")
@NoArgsConstructor
@AllArgsConstructor
public class CategoryBrandEntity {

    private Integer categoryId;

    private Integer brandId;


}
