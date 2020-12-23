package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.mapper.CategoryMapper;
import com.baidu.shop.service.CategoryService;
import com.google.gson.JsonObject;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * 2 * @ClassName CategoryServiceImpl
 * 3 * @Description: TODO
 * 4 * @Author wangxin
 * 5 * @Date 2020/12/22
 * 6 * @Version V1.0
 * 7
 **/
@RestController
public class CategoryServiceImpl extends BaseApiService implements CategoryService {

    @Resource
    private CategoryMapper mapper;

    @Override
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setParentId(pid);
        List<CategoryEntity> list = mapper.select(categoryEntity);

        return this.setResultSuccess(list);
    }

    @Override
    public Result<JsonObject> deleteById(Integer id) {

        if (id == null  || id <= 0){
           return this.setResultError("id不合法");
        }
        //查询当前的数据是不是父节点(就是isparentid == 1 )如果等于一就直接返回
        CategoryEntity categoryEntity = mapper.selectByPrimaryKey(id);

        if(categoryEntity == null){
            return this.setResultError("找不到当前数据");
        }

        if(categoryEntity.getIsParent() == 1){
           return  this.setResultError("当前id为父节点不能被删除");
        }


        //根据当前数据查查到他们呢的父节点是谁，如果只有一条数据删了了那么他的父节点就会变成叶子节点
        Integer parentId = categoryEntity.getParentId();

        Example example = new Example(CategoryEntity.class);

        example.createCriteria().andEqualTo("parentId",parentId);

        List<CategoryEntity> categoryEntities = mapper.selectByExample(example);

        if(categoryEntities.size() == 1){
            CategoryEntity categoryEntity1 = new CategoryEntity();
            categoryEntity1.setIsParent(0);
            categoryEntity1.setId(parentId);
            mapper.updateByPrimaryKeySelective(categoryEntity1);
        }


        mapper.deleteByPrimaryKey(id);
        return this.setResultSuccess("操作成功");
    }
}
