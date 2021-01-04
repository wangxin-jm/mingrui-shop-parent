package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 2 * @ClassName specificationEntity
 * 3 * @Description: TODO
 * 4 * @Author wangxin
 * 5 * @Date 2021/1/4
 * 6 * @Version V1.0
 * 7
 **/
@Table(name = "tb_spec_group")
@Data
public class SpecificationEntity {

    @Id
    private Integer id;

    private Integer cid;

    private  String name;
}
