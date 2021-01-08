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
    public Result<JSONObject> updateXia(SpuDTO spuDTO) {
        this.sahngjiaAndxiajia(spuDTO,0);
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> updateShang(SpuDTO spuDTO) {
        this.sahngjiaAndxiajia(spuDTO,1);
        return this.setResultSuccess();
    }

    //上架下架
    public void sahngjiaAndxiajia(SpuDTO spuDTO,Integer i){
        SpuEntity spuEntity = BaiduBeanUtil.beanUtil(spuDTO, SpuEntity.class);
        spuEntity.setSaleable(i);
        spuMapper.updateByPrimaryKeySelective(spuEntity);
    }
    @Override
    @Transactional
    public Result<JSONObject> delete(Integer id) {
        spuMapper.deleteByPrimaryKey(id);
        spuDetailMapper.deleteByPrimaryKey(id);

        this.sahnchu(id);

        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<JSONObject> update(SpuDTO spuDTO) {

        final Date date = new Date();
        SpuEntity spuEntity = BaiduBeanUtil.beanUtil(spuDTO, SpuEntity.class);
        spuMapper.updateByPrimaryKeySelective(spuEntity);

        SpuDetailDTO spuDetail = spuDTO.getSpuDetail();

        spuDetailMapper.updateByPrimaryKeySelective(BaiduBeanUtil.beanUtil(spuDetail,SpuDetailEntity.class));


        this.sahnchu(spuEntity.getId());

        this.saveSkuAndStock(spuDTO,spuEntity.getId(),date);

        return this.setResultSuccess();
    }

    public void sahnchu(Integer spuId){
        //需要先查询出来,然后在删除,然后在新增
        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId",spuId);
        List<SkuEntity> skuEntities = SkuMapper.selectByExample(example);
        List<Long> collect = skuEntities.stream().map(SkuEntity ->  SkuEntity.getId()).collect(Collectors.toList());

        //删除表中的数据,因为是一对多的关系,关系到多张表的数据
        SkuMapper.deleteByIdList(collect);
        StockMapper.deleteByIdList(collect);
    }


    @Override
    public Result<SpuDetailEntity> spuDetailList(Integer spuId) {


        return this.setResultSuccess(spuDetailMapper.selectByPrimaryKey(spuId));
    }

    @Override
    public Result<List<SkuDTO>> getSpuSkuByIdList(Integer spuId) {
       List<SkuDTO> list = SkuMapper.getSpuSkuByIdList(spuId);

        return this.setResultSuccess(list);
    }





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

        this.saveSkuAndStock(spuDTO,spuEntity.getId(),date);

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


    //封装新增方法
    public void saveSkuAndStock(SpuDTO spuDTO,Integer spuId,Date date){
        List<SkuDTO> skus = spuDTO.getSkus();
        skus.stream().forEach(skuDTO ->{
            SkuEntity skuEntity = BaiduBeanUtil.beanUtil(skuDTO, SkuEntity.class);
            skuEntity.setSpuId(spuId);
            skuEntity.setCreateTime(date);
            skuEntity.setLastUpdateTime(date);
            SkuMapper.insertSelective(skuEntity);

            StockEntity stockEntity = new StockEntity();
            stockEntity.setSkuId(skuEntity.getId());
            stockEntity.setStock(skuDTO.getStock());
            StockMapper.insertSelective(stockEntity);
        });

    }



}
