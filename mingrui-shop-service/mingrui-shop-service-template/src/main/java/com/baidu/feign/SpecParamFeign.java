package com.baidu.feign;

import com.baidu.shop.service.SpecParamService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "xxx-server",contextId = "SpecParamFeign")
public interface SpecParamFeign extends SpecParamService {
}
