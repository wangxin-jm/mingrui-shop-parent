package com.baidu.feign;

import com.baidu.shop.service.SpuService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "xxx-server",contextId = "GoodsFeign")
public interface GoodsFeign extends SpuService {

}
