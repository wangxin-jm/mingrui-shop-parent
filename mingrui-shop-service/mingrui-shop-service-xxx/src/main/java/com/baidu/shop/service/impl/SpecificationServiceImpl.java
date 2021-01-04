package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.entity.SpecificationEntity;
import com.baidu.shop.mapper.SpecGroupMapper;
import com.baidu.shop.mapper.SpenParamMapper;
import com.baidu.shop.service.SpecificationService;
import com.baidu.shop.utils.BaiduBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 2 * @ClassName SpecificationServiceImpl
 * 3 * @Description: TODO
 * 4 * @Author wangxin
 * 5 * @Date 2021/1/4
 * 6 * @Version V1.0
 * 7
 **/
@RestController
public class SpecificationServiceImpl extends BaseApiService implements SpecificationService {

    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpenParamMapper spenParamMapper;


    @Override
    public Result<List<SpecificationEntity>> getSpecList(SpecGroupDTO specGroupDTO) {


        Example example = new Example(SpecificationEntity.class);
        example.createCriteria().andEqualTo("cid",BaiduBeanUtil.beanUtil(specGroupDTO, SpecificationEntity.class).getCid());
        List<SpecificationEntity> specificationEntities = specGroupMapper.selectByExample(example);

        return this.setResultSuccess(specificationEntities);
    }

    @Override
    @Transactional
    public Result<JSONObject> save(SpecGroupDTO specGroupDTO) {
        specGroupMapper.insertSelective(BaiduBeanUtil.beanUtil(specGroupDTO,SpecificationEntity.class));

        return this.setResultSuccess("新增成功");
    }

    @Override
    @Transactional
    public Result<JSONObject> update(SpecGroupDTO specGroupDTO) {
        specGroupMapper.updateByPrimaryKeySelective(BaiduBeanUtil.beanUtil(specGroupDTO,SpecificationEntity.class));

        return this.setResultSuccess("新增成功");
    }

    @Override
    @Transactional
    public Result<JSONObject> deletes(Integer id) {

        Example example = new Example(SpecParamEntity.class);
        example.createCriteria().andEqualTo("groupId",id);
        List<SpecParamEntity> specParamEntities = spenParamMapper.selectByExample(example);
        if(specParamEntities.size() >= 1) return this.setResultError("当前规格有规格参数不能被删除");


        specGroupMapper.deleteByPrimaryKey(id);
        return this.setResultSuccess();
    }

}
