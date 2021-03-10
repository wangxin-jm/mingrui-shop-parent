package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 2 * @ClassName SpuService
 * 3 * @Description: TODO
 * 4 * @Author wangxin
 * 5 * @Date 2021/1/5
 * 6 * @Version V1.0
 * 7
 **/
@Api(tags = "商品接口")
public interface SpuService {

    @ApiOperation("商品查询")
    @GetMapping("goods/list")
    Result<List<SpuDTO>> list(@SpringQueryMap SpuDTO spuDTO);

    @ApiOperation("新增商品")
    @PostMapping("goods/save")
    Result<JSONObject> save(@RequestBody SpuDTO spuDTO);

    @ApiOperation("修改商品")
    @PutMapping("goods/save")
    Result<JSONObject> update(@RequestBody SpuDTO spuDTO);

    @ApiOperation("根据id查询SpuDetail")
    @GetMapping("goods/spuDetailList")
    Result<SpuDetailEntity> spuDetailList(@RequestParam Integer spuId);

    @ApiOperation("")
    @GetMapping("goods/getSpuSkuByIdList")
    Result<List<SkuDTO>> getSpuSkuByIdList(@RequestParam Integer spuId);

    @ApiOperation("删除商品")
    @DeleteMapping("goods/delete")
    Result<JSONObject> delete(Integer id);


    @ApiOperation("下架商品")
    @PutMapping("goos/xiajia")
    Result<JSONObject> updateXia(@Validated({MingruiOperation.Update.class}) @RequestBody SpuDTO spuDTO);

    @ApiOperation("上架商品")
    @PutMapping("goos/shangjia")
    Result<JSONObject> updateShang(@Validated({MingruiOperation.Update.class})@RequestBody SpuDTO spuDTO);


}
