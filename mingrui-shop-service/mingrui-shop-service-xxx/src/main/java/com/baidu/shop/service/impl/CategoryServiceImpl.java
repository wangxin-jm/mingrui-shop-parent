package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.mapper.CategoryMapper;
import com.baidu.shop.service.CategoryService;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private CategoryBrandMapper categoryBrandMapper;


    @Override
    public Result<List<CategoryEntity>> getSelectBrandById(Integer brandId) {
        List<CategoryEntity> categoryByBrandId = mapper.getCategoryByBrandId(brandId);

        return this.setResultSuccess(categoryByBrandId);
    }


    @Override
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setParentId(pid);
        List<CategoryEntity> list = mapper.select(categoryEntity);

        return this.setResultSuccess(list);
    }



    @Override
    @Transactional
    public Result<JsonObject> deleteById(Integer id) {

//        if (UtilNull.isNull(id)  || id <= 0) return this.setResultError("id不合法");
//
//        //查询当前的数据是不是父节点(就是isparent == 1 )如果等于一就直接返回
//        CategoryEntity categoryEntity = mapper.selectByPrimaryKey(id);
//
//        if(UtilNull.isNull(categoryEntity)) return this.setResultError("找不到当前数据");
//
//
//        if(categoryEntity.getIsParent() == 1) return  this.setResultError("当前id为父节点不能被删除");
//
//        //根据当前数据查查到他们呢的父节点是谁，如果只有一条数据删了了那么他的父节点就会变成叶子节点
//        Integer parentId = categoryEntity.getParentId();
//
//        Example example = new Example(CategoryEntity.class);
//
//        example.createCriteria().andEqualTo("parentId",parentId);
//
//        List<CategoryEntity> categoryEntities = mapper.selectByExample(example);
//
//        if(categoryEntities.size() <= 1){
//            CategoryEntity categoryEntity1 = new CategoryEntity();
//            categoryEntity1.setIsParent(0);
//            categoryEntity1.setId(parentId);
//            mapper.updateByPrimaryKeySelective(categoryEntity1);
//        }


        if (id == null || id <= 0) {
            return this.setResultError("id不合法");
        }
        //得到查询的那一条数据
        CategoryEntity categoryEntity = mapper.selectByPrimaryKey(id);

        //在判断数据存不存在
        if (categoryEntity == null) {
            return this.setResultError("数据不存在");
        }

        //如果这条数据存在,判断这条数据是不是父节点 isparent = 1是父节点
        if (categoryEntity.getIsParent() == 1) {
            return this.setResultError("当前节点为父节点不能被删除");
        }

        //如果当前分类被品牌绑定的话不能被删除 --> 通过分类id查询中间表是否有数据 true : 当前分类不能被删除 false:继续执行
        Example example1 = new Example(CategoryBrandEntity.class);
        example1.createCriteria().andEqualTo("categoryId",id);
        List<CategoryBrandEntity> categoryBrandEntities = categoryBrandMapper.selectByExample(example1);
        if(categoryBrandEntities.size() >= 1) return this.setResultError("当前分类已被品牌绑定");


        //如果不是父节点在查询parentId有几条数据
        Example example = new Example(CategoryEntity.class);
        example.createCriteria().andEqualTo("parentId",categoryEntity.getParentId());
        List<CategoryEntity> categoryEntities = mapper.selectByExample(example);


        if (categoryEntities.size() <= 1 ) {
            CategoryEntity categoryEntity1 = new CategoryEntity();
            categoryEntity1.setIsParent(0);
            categoryEntity1.setId(categoryEntity.getParentId());
            mapper.updateByPrimaryKeySelective(categoryEntity1);

        }


        mapper.deleteByPrimaryKey(id);
        return this.setResultSuccess("操作成功");
    }

    @Override
    @Transactional
    public Result<JsonObject> updateById(CategoryEntity categoryEntity) {

            mapper.updateByPrimaryKeySelective(categoryEntity);
        return this.setResultSuccess("修改成功");
    }

    @Override
    @Transactional
    public Result<JsonObject> save(CategoryEntity categoryEntity) {

        Integer parentId = categoryEntity.getParentId();

        CategoryEntity categoryEntity1 = mapper.selectByPrimaryKey(parentId);

        if(categoryEntity1 == null ){
            return this.setResultError("操作失败");
        }

        if(categoryEntity1.getIsParent() == 0){
            CategoryEntity categoryEntity2 = new CategoryEntity();
            categoryEntity2.setIsParent(1);
            categoryEntity2.setId(parentId);
            mapper.updateByPrimaryKeySelective(categoryEntity2);
        }

        mapper.insertSelective(categoryEntity);
        return this.setResultSuccess("新增成功");
    }
}
