package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.mapper.SpenParamMapper;
import com.baidu.shop.service.SpecParamService;
import com.baidu.shop.utils.BaiduBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 2 * @ClassName SpecParamServiceImpl
 * 3 * @Description: TODO
 * 4 * @Author wangxin
 * 5 * @Date 2021/1/4
 * 6 * @Version V1.0
 * 7
 **/
@RestController
public class SpecParamServiceImpl  extends BaseApiService implements SpecParamService {

    @Autowired
    private SpenParamMapper spenParamMapper;

    @Override
    public Result<SpecParamEntity> list(SpecParamDTO specParamDTO) {

        Example example = new Example(SpecParamEntity.class);
        example.createCriteria().andEqualTo("groupId", BaiduBeanUtil.beanUtil(specParamDTO,SpecParamEntity.class).getGroupId());
        List<SpecParamEntity> specParamEntities = spenParamMapper.selectByExample(example);

        return this.setResultSuccess(specParamEntities);
    }

    @Override
    @Transactional
    public Result<JSONObject> save(SpecParamDTO specParamDTO) {

        spenParamMapper.insertSelective(BaiduBeanUtil.beanUtil(specParamDTO,SpecParamEntity.class));
        return this.setResultSuccess("成功");
    }

    @Override
    @Transactional
    public Result<JSONObject> update(SpecParamDTO specParamDTO) {
        spenParamMapper.updateByPrimaryKeySelective(BaiduBeanUtil.beanUtil(specParamDTO,SpecParamEntity.class));
        return this.setResultSuccess("成功");
    }

    @Override
    @Transactional
    public Result<JSONObject> delete(Integer id) {
        spenParamMapper.deleteByPrimaryKey(id);
        return this.setResultSuccess("成功");
    }
}
