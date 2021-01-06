package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.SpuEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryMapper;
import com.baidu.shop.mapper.SpuMapper;
import com.baidu.shop.service.SpuService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.util.UtilNull;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 2 * @ClassName SpuServiceImpl
 * 3 * @Description: TODO
 * 4 * @Author wangxin
 * 5 * @Date 2021/1/5
 * 6 * @Version V1.0
 * 7
 **/
@RestController
public class SpuServiceImpl extends BaseApiService implements SpuService {

    @Resource
    private SpuMapper spuMapper;

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public Result<List<SpuDTO>> list(SpuDTO spuDTO) {

        if(UtilNull.isNotNull(spuDTO.getPage()) && UtilNull.isNotNull(spuDTO.getRows()))
        PageHelper.startPage(spuDTO.getPage(),spuDTO.getRows());


        Example example = new Example(SpuEntity.class);
        Example.Criteria criteria = example.createCriteria();

        if(UtilNull.isNotNull(BaiduBeanUtil.beanUtil(spuDTO,SpuEntity.class).getSaleable())
                && BaiduBeanUtil.beanUtil(spuDTO,SpuEntity.class).getSaleable() <2){
            criteria.andEqualTo("saleable",BaiduBeanUtil.beanUtil(spuDTO,SpuEntity.class).getSaleable());
        }

        criteria.andLike("title","%"+spuDTO.getTitle()+"%");

        List<SpuEntity> spuEntities = spuMapper.selectByExample(example);

        List<SpuDTO> spuDTOList = spuEntities.stream().map(spuEntity -> {
            SpuDTO spuDTO1 = BaiduBeanUtil.beanUtil(spuEntity, SpuDTO.class);
//            //查询分类name
//            CategoryEntity categoryEntity = categoryMapper.selectByPrimaryKey(spuEntity.getCid1());
//            CategoryEntity categoryEntity2 = categoryMapper.selectByPrimaryKey(spuEntity.getCid2());
//            CategoryEntity categoryEntity3= categoryMapper.selectByPrimaryKey(spuEntity.getCid3());
//            spuDTO1.setCategoryName(categoryEntity.getName()+"-"+categoryEntity2.getName()+"-"+categoryEntity3.getName());


            //使用多查询,查询出三条数据
            List<CategoryEntity> categoryEntities = categoryMapper.selectByIdList(Arrays.asList(spuDTO1.getCid1(), spuDTO1.getCid2(), spuDTO1.getCid3()));
            //把三条数据的name用/拼接
            String collect = categoryEntities.stream().map(CategoryEntity -> CategoryEntity.getName()).collect(Collectors.joining("/"));
            spuDTO1.setCategoryName(collect);

            //////与上面结果一样
            //spuDTO1.setCategoryName(categoryMapper
            // .selectByIdList(Arrays.asList(spuDTO1.getCid1(), spuDTO1.getCid2(), spuDTO1.getCid3()))
            // .stream().map(CategoryEntity -> CategoryEntity.getName())
            // .collect(Collectors.joining("-")));

            //根据商品中的brandId查询出品牌name
            BrandEntity brandEntity = brandMapper.selectByPrimaryKey(spuEntity.getBrandId());
            spuDTO1.setBrandName(brandEntity.getName());

            return spuDTO1;
        }).collect(Collectors.toList());


        PageInfo<SpuEntity> spuEntityPageInfo = new PageInfo<>(spuEntities);


        return this.setResult(HTTPStatus.OK,spuEntityPageInfo.getTotal()+"",spuDTOList);
    }

}
