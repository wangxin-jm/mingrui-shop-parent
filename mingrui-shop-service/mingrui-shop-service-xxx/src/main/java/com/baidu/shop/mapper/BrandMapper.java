package com.baidu.shop.mapper;

import com.baidu.shop.entity.BrandEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<BrandEntity> {


    @Select(value="SELECT * FROM  `tb_brand` WHERE id IN (SELECT t.`brand_id` FROM `tb_category_brand` t WHERE t.`category_id`= #{cid})")
    List<BrandEntity> getListCategoryBrand(Integer cid);
}
