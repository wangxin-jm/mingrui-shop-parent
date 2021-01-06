package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpuDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 2 * @ClassName SpuService
 * 3 * @Description: TODO
 * 4 * @Author wangxin
 * 5 * @Date 2021/1/5
 * 6 * @Version V1.0
 * 7
 **/
@Api("商品接口")
public interface SpuService {

    @ApiOperation("商品查询")
    @GetMapping("goods/list")
    Result<List<SpuDTO>> list(SpuDTO spuDTO);
}
