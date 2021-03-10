package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

@Api(tags ="页面静态化接口 ")
public interface TemplateService {
    @GetMapping(value = "template/createStaticHTMLTemplate")
    @ApiOperation(value="根剧spuId删除一个html文件")
    Result<JSONObject> createStaticHTMLTemplate(Integer spuId);

    @ApiOperation(value="创建全部的html文件")
    @GetMapping(value = "template/initStaticHTMLTemplate")
    Result<JSONObject> initStaticHTMLTemplate();

    @ApiOperation(value="删除全部的文件")
    @GetMapping(value = "template/clearStaticHTMLTemplate")
    Result<JSONObject> clearStaticHTMLTemplate();

    @ApiOperation(value="根据id创建html文件")
    @GetMapping(value = "template/deleteStaticHTMLTemplate")
    Result<JSONObject> deleteStaticHTMLTemplate(Integer spuId);

}
