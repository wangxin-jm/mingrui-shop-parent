package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.dto.SpuDetailDTO;
import com.baidu.shop.entity.*;
import com.baidu.shop.mapper.*;
import com.baidu.shop.service.SpuService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.util.UtilNull;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
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

    @Resource
    private SpuDetailMapper spuDetailMapper;

    @Resource
    private StockMapper StockMapper;

    @Resource
    private SkuMapper SkuMapper;

    @Override
    @Transactional
    public Result<JSONObject> save(SpuDTO spuDTO) {

        final Date date = new Date();
        SpuEntity spuEntity = BaiduBeanUtil.beanUtil(spuDTO, SpuEntity.class);
        spuEntity.setSaleable(1);
        spuEntity.setValid(1);
        spuEntity.setCreateTime(date);
        spuEntity.setLastUpdateTime(date);
        spuMapper.insertSelective(spuEntity);

        SpuDetailDTO spuDetail = spuDTO.getSpuDetail();
        SpuDetailEntity spuDetailEntity1 = BaiduBeanUtil.beanUtil(spuDetail, SpuDetailEntity.class);

        spuDetailEntity1.setSpuId(spuEntity.getId());

        spuDetailMapper.insertSelective(spuDetailEntity1);
        List<SkuDTO> skus = spuDTO.getSkus();

        skus.stream().forEach(skuDTO -> {
            SkuEntity skuEntity = BaiduBeanUtil.beanUtil(skuDTO, SkuEntity.class);
            skuEntity.setSpuId(spuEntity.getId());
            skuEntity.setCreateTime(date);
            skuEntity.setLastUpdateTime(date);
            SkuMapper.insertSelective(skuEntity);

            StockEntity stockEntity = new StockEntity();
            stockEntity.setSkuId(skuEntity.getId());
            Integer stock = skuDTO.getStock();
            stockEntity.setStock(stock);
            StockMapper.insertSelective(stockEntity);


        } );

        return this.setResultSuccess();
    }


    @Override
    public Result<List<SpuDTO>> list(SpuDTO spuDTO) {

        if(UtilNull.isNotNull(spuDTO.getPage()) && UtilNull.isNotNull(spuDTO.getRows()))
        PageHelper.startPage(spuDTO.getPage(),spuDTO.getRows());

        if(!StringUtils.isEmpty(spuDTO.getSort()) && UtilNull.isNotNull(spuDTO.getOrder())){
            PageHelper.orderBy(spuDTO.getOrderByClause());
        }

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
