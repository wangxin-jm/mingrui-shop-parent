package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.PinyinUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 2 * @ClassName BrandServiceImpl
 * 3 * @Description: TODO
 * 4 * @Author wangxin
 * 5 * @Date 2020/12/25
 * 6 * @Version V1.0
 * 7
 **/
@RestController
public class BrandServiceImpl extends BaseApiService implements BrandService {

    @Resource
    private BrandMapper mapper;

    @Resource
    private CategoryBrandMapper categoryBrandMapper;



    @Override
    @Transactional
    public Result<JSONObject> delete(Integer id) {
            mapper.deleteByPrimaryKey(id);

//           再删除关系表中的数据根据id
//        Example example = new Example(CategoryBrandEntity.class);
//        example.createCriteria().andEqualTo("brandId",id);
//        categoryBrandMapper.deleteByExample(example);
        this.deleteCategoryBrandBybrandId(id);

        return this.setResultSuccess("删除成功");
    }


    @Override
    @Transactional
    public Result<JSONObject> update(BrandDTO brandDTO) {
        BrandEntity brandEntity = BaiduBeanUtil.beanUtil(brandDTO, BrandEntity.class);
        brandEntity.setLetter(PinyinUtil.getUpperCase(brandEntity.getName(),false).toCharArray()[0]);
        mapper.updateByPrimaryKeySelective(brandEntity);

        //删除中间表的数据
//        Example example = new Example(CategoryBrandEntity.class);
//        example.createCriteria().andEqualTo("brandId",brandEntity.getId());
//        categoryBrandMapper.deleteByExample(example);
        this.deleteCategoryBrandBybrandId(brandEntity.getId());

//        //批量新增
//        String categories = brandDTO.getCategories();//得到分类集合字符串
//        if(StringUtils.isEmpty(brandDTO.getCategories())) return this.setResultError("");
//
//
//        //判断分类集合字符串中是否包含,
//        if(categories.contains(",")){//多个分类 --> 批量新增
//
//            categoryBrandMapper.insertList(
//                    Arrays.asList(categories.split(","))
//                            .stream()
//                            .map(categoryIdStr -> new CategoryBrandEntity(Integer.valueOf(categoryIdStr)
//                                    ,brandEntity.getId()))
//                            .collect(Collectors.toList())
//            );
//
//        }else{//普通单个新增
//
//            CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
//            categoryBrandEntity.setBrandId(brandEntity.getId());
//            categoryBrandEntity.setCategoryId(Integer.valueOf(categories));
//
//            categoryBrandMapper.insertSelective(categoryBrandEntity);
//        }

        this.categoryBrandSaveList(brandDTO.getCategories(),brandEntity.getId());
        return this.setResultSuccess();
    }


    @Override
    public Result<PageInfo<BrandEntity>> list(BrandDTO brandDTO) {
        //分页 给他们赋值
        PageHelper.startPage(brandDTO.getPage(),brandDTO.getRows());

//            if(!StringUtils.isEmpty(brandDTO.getOrder())){
////                    String paixu = "";
////                    if (brandDTO.getOrder().equals("true")){
////                        paixu = "desc";
////                    }
//                PageHelper.orderBy(brandDTO.getSort()+" "+ paixu );
//            }
        if (!StringUtils.isEmpty(brandDTO.getOrder())) PageHelper.orderBy(brandDTO.getOrderByClause());



        BrandEntity brandEntity = BaiduBeanUtil.beanUtil(brandDTO, BrandEntity.class);

        Example example = new Example(BrandEntity.class);
        example.createCriteria().andLike("name","%"+brandEntity.getName()+"%");
        List<BrandEntity> brandEntities = mapper.selectByExample(example);

        PageInfo<BrandEntity> objectPageInfo = new PageInfo<>(brandEntities);


        return this.setResultSuccess(objectPageInfo);
    }

    @Override
    @Transactional
    public Result<JSONObject> save(BrandDTO brandDTO) {
        BrandEntity brandEntity = BaiduBeanUtil.beanUtil(brandDTO, BrandEntity.class);

        //char[] chars = PinyinUtil.getUpperCase(brandEntity.getName(), false).toCharArray();
        brandEntity.setLetter(PinyinUtil.getUpperCase(brandEntity.getName(), false).toCharArray()[0]);
        //在品牌数据表增加
        if(brandEntity.getId() != null) brandEntity.setId(null);
        mapper.insertSelective(brandEntity);

//        //维护中间表数据
//        String categories = brandDTO.getCategories();//得到分类集合字符串
//        if(StringUtils.isEmpty(brandDTO.getCategories())) return this.setResultError("");
//
//        List<CategoryBrandEntity> categoryBrandEntities = new ArrayList<>();
//
//        if (categories.contains(",")) {//多个分类-->批量新增
//            String[] split = categories.split(",");
//            for (int i = 0; i <split.length ; i++) {
//                CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
//                categoryBrandEntity.setBrandId(brandEntity.getId());
//                categoryBrandEntity.setCategoryId(Integer.parseInt(split[i]));
//                categoryBrandEntities.add(categoryBrandEntity);
//            }
//            categoryBrandMapper.insertList(categoryBrandEntities);
//        }else{
//            CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
//            categoryBrandEntity.setBrandId(brandEntity.getId());
//            categoryBrandEntity.setCategoryId(Integer.parseInt(categories));
//            categoryBrandMapper.insertSelective(categoryBrandEntity);
//        }
            this.categoryBrandSaveList(brandDTO.getCategories(),brandEntity.getId());
        return this.setResultSuccess();
    }

        //删除中间表的提出来的方法
    private void deleteCategoryBrandBybrandId(Integer id){
        Example example = new Example(CategoryBrandEntity.class);
        example.createCriteria().andEqualTo("brandId",id);
        categoryBrandMapper.deleteByExample(example);
    }

    //批量新增的方法
    private void categoryBrandSaveList(String categories,Integer id){
//        String categories = brandDTO.getCategories();//得到分类集合字符串
        if(StringUtils.isEmpty(categories)) throw  new RuntimeException("失败");


        //判断分类集合字符串中是否包含,
        if(categories.contains(",")){//多个分类 --> 批量新增

            categoryBrandMapper.insertList(
                    Arrays.asList(categories.split(","))
                            .stream()
                            .map(categoryIdStr -> new CategoryBrandEntity(Integer.valueOf(categoryIdStr)
                                    ,id))
                            .collect(Collectors.toList())
            );

        }else{//普通单个新增

            CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
            categoryBrandEntity.setBrandId(id);
            categoryBrandEntity.setCategoryId(Integer.valueOf(categories));

            categoryBrandMapper.insertSelective(categoryBrandEntity);
        }
    }



}
