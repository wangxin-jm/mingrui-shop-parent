package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.entity.SpecificationEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "规格组接口")
public interface SpecificationService {

    @ApiOperation("通过查询")
    @GetMapping("spec/list")
    Result<List<SpecificationEntity>> getSpecList(SpecGroupDTO specGroupDTO);

    @ApiOperation("新增")
    @PostMapping("spec/save")
    Result<JSONObject> save(@Validated({MingruiOperation.Add.class})@RequestBody SpecGroupDTO specGroupDTO);

    @ApiOperation("修改")
    @PutMapping("spec/save")
    Result<JSONObject> update(@Validated({MingruiOperation.Update.class})@RequestBody SpecGroupDTO specGroupDTO);

    @ApiOperation("删除")
    @DeleteMapping("spec/delete")
    Result<JSONObject> deletes(Integer id);
}
