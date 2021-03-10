package com.baidu.service.impl;

import com.baidu.feign.*;
import com.baidu.service.PageService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.*;
import com.baidu.shop.entity.*;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 2 * @ClassName PageServiceImpl
 * 3 * @Description: TODO
 * 4 * @Author wangxin
 * 5 * @Date 2021/3/8
 * 6 * @Version V1.0
 * 7
 **/
//@Service
public class PageServiceImpl implements PageService {


//    @Autowired
    private GoodsFeign goodsFeign;

//    @Autowired
    private SpecificationFeign specificationFeign;

//    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

//    @Autowired
    private BrandFeign brandFeign;

//    @Autowired
    private CategoryFeign categoryFeign;

//    @Autowired
    private SpecParamFeign specParamFeign;


    @Override
    public Map<String, Object> getGoodsInfo(Integer spuId) {

        Map<String, Object> goodsMap = new HashMap<>();
        //查询spu
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);

        Result<List<SpuDTO>> spuResult = goodsFeign.list(spuDTO);
        SpuDTO spuDTO1 = null;
        if(spuResult.isSuccess()){
            spuDTO1 = spuResult.getData().get(0);


            goodsMap.put("spuInfo",spuDTO1);
        }
        //在查询spuDetail
        Result<SpuDetailEntity> spuDetailResult = goodsFeign.spuDetailList(spuId);
        if(spuDetailResult.isSuccess()){
            SpuDetailEntity data = spuDetailResult.getData();
            goodsMap.put("spuDetail",data);
        }

        //分类信息
        Result<List<CategoryEntity>> categoryResult = categoryFeign.getCategoryByid(
                String.join(","
                        , Arrays.asList(spuDTO1.getCid1() + "", spuDTO1.getCid2() + ""
                                , spuDTO1.getCid3() + ""))
        );
        if(categoryResult.isSuccess()){
            List<CategoryEntity> data = categoryResult.getData();
            goodsMap.put("categoryInfo",data);
        }
        //品牌信息
        BrandDTO brandDTO = new BrandDTO();
        brandDTO.setId(spuDTO1.getBrandId());
        Result<PageInfo<BrandEntity>> brandRreslt = brandFeign.list(brandDTO);
        if (brandRreslt.isSuccess()){

            goodsMap.put("brandInfo", brandRreslt.getData().getList().get(0));

        }
        //sku
        Result<List<SkuDTO>> spuSkuResult = goodsFeign.getSpuSkuByIdList(spuId);
        if (spuSkuResult.isSuccess()){

            goodsMap.put("skus",spuSkuResult.getData());
        }

        //规格组,规格参数(通用)
        SpecGroupDTO specGroupDTO = new SpecGroupDTO();
        specGroupDTO.setId(spuDTO.getCid3());
        Result<List<SpecificationEntity>> specGroupResult = specificationFeign.getSpecList(specGroupDTO);
        if(specGroupResult.isSuccess()){

            List<SpecificationEntity> specGroupList = specGroupResult.getData();
            List<SpecGroupDTO> specGroupAndParamResult = specGroupList.stream().map(specGroup -> {
                SpecGroupDTO specGroupDTO1 = BaiduBeanUtil.beanUtil(specGroup, SpecGroupDTO.class);

                SpecParamDTO specParamDTO = new SpecParamDTO();
                specParamDTO.setGroupId(specGroupDTO1.getId());
                specParamDTO.setGeneric(true);
                Result<List<SpecParamEntity>> specParamResult = specParamFeign.list(specParamDTO);
                if (specParamResult.isSuccess()) {
                    specGroupDTO1.setSpecList(specParamResult.getData());
                }

                return specGroupDTO1;
            }).collect(Collectors.toList());
            goodsMap.put("specGroupAndParam",specGroupAndParamResult);

        }

        //特殊规格
        SpecParamDTO specParamDTO = new SpecParamDTO();

        specParamDTO.setCid(spuDTO.getCid3());
        specParamDTO.setGeneric(false);
        Result<List<SpecParamEntity>> specParamResult = specParamFeign.list(specParamDTO);
        if(specParamResult.isSuccess()){
            List<SpecParamEntity> specParamResultData = specParamResult.getData();
            Map<Integer, String> specParamMap = new HashMap<>();
            specParamResultData.stream().forEach(specParam -> specParamMap.put(
                    specParam.getId(),specParam.getName())
            );
            goodsMap.put("specParamMap",specParamMap);
        }



        return goodsMap;
    }
}
