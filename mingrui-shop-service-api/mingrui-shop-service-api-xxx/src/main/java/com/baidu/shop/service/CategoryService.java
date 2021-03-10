package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品分类接口")
public interface CategoryService {

    @ApiOperation(value = "通过查询商品分类")
    @GetMapping(value="category/list")
    Result<List<CategoryEntity>> getCategoryByPid(@RequestParam Integer pid);

    @ApiOperation(value = "通过品牌查询分类id")
    @GetMapping(value="category/brand")
    Result<List<CategoryEntity>> getSelectBrandById(Integer pid);

    @ApiOperation(value="删除不是父节点的")
    @DeleteMapping(value="category/delete")
    Result<JsonObject> deleteById(Integer id);

    @ApiOperation(value="修改")
    @PutMapping("category/upfate")
    Result<JsonObject> updateById(@Validated({MingruiOperation.Update.class}) @RequestBody CategoryEntity  categoryEntity);

    @ApiOperation("新增数据")
    @PostMapping("category/save")
    Result<JsonObject> save(@Validated({MingruiOperation.Add.class}) @RequestBody CategoryEntity categoryEntity);


    @ApiOperation(value = "通过分类id查询分类信息")
    @GetMapping(value="category/getCategoryByid")
    Result<List<CategoryEntity>> getCategoryByid(@RequestParam String ids);
}
