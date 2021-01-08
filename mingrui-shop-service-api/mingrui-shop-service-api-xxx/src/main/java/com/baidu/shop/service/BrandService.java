package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "品牌分类接口")
public interface BrandService {

    @GetMapping(value = "brand/list")
    @ApiOperation(value ="查询商品")
    Result<PageInfo<BrandEntity>> list(BrandDTO brandDTO);

    @PostMapping(value="brand/save")
    @ApiOperation(value="新增数据")
    Result<JSONObject> save(@Validated({MingruiOperation.Add.class})@RequestBody BrandDTO brandDTO);

    @PutMapping(value="brand/save")
    @ApiOperation(value="修改数据数据")
    Result<JSONObject> update(@Validated({MingruiOperation.Update.class})@RequestBody BrandDTO brandDTO);

    @DeleteMapping(value="brand/delete")
    @ApiOperation(value="删除品牌分类")
    Result<JSONObject> delete(Integer id);

    @GetMapping(value = "brand/getListCategoryBrand")
    @ApiOperation(value ="通过分类id查询品牌")
    Result<List<BrandEntity>> getListCategoryBrand(Integer cid);
}
