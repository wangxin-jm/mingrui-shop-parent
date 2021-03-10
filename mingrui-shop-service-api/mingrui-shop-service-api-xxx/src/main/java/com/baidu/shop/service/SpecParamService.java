package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "规格参数接口")
public interface SpecParamService {

    @ApiOperation("规格参数查询")
    @GetMapping("specParam/list")
    Result <List<SpecParamEntity>> list(@SpringQueryMap SpecParamDTO specParamDTO);


    @ApiOperation("规格参数新增")
    @PostMapping("specParam/save")
    Result<JSONObject> save(@Validated({MingruiOperation.Add.class})@RequestBody SpecParamDTO specParamDTO);

    @ApiOperation("规格参数修改")
    @PutMapping("specParam/save")
    Result<JSONObject> update(@Validated({MingruiOperation.Update.class})@RequestBody SpecParamDTO specParamDTO);

    @ApiOperation("删除规格参数")
    @DeleteMapping("specParam/delete")
    Result<JSONObject> delete(Integer id);


}
