package com.baidu.feign;

import com.baidu.shop.service.BrandService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value ="xxx-server",contextId = "BrandFeign")
public interface BrandFeign extends BrandService {
}