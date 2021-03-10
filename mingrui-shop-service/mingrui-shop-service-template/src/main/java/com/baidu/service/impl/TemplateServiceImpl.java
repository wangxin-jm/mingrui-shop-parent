package com.baidu.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.feign.*;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.*;
import com.baidu.shop.entity.*;
import com.baidu.shop.service.TemplateService;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 2 * @ClassName TemplateServiceImpl
 * 3 * @Description: TODO
 * 4 * @Author wangxin
 * 5 * @Date 2021/3/9
 * 6 * @Version V1.0
 * 7
 **/
@RestController
public class TemplateServiceImpl extends BaseApiService implements TemplateService {

    private final Integer CREATE_STATIC_HTML = 1;

    private final Integer DELETE_STATIC_HTML = 2;

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private SpecificationFeign specificationFeign;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private BrandFeign brandFeign;

    @Autowired
    private CategoryFeign categoryFeign;

    @Autowired
    private SpecParamFeign specParamFeign;

    @Autowired
    private TemplateEngine templateEngine;

    @Value(value = "${mrshop.static.html.path}")
    private String htmlPath;

    @Override
    public Result<JSONObject> createStaticHTMLTemplate(Integer spuId) {
        //得到要渲染的数据
        Map<String, Object> goodsInfo = this.getGoodsInfo(spuId);
        Context context = new Context();
        context.setVariables(goodsInfo);

        File file = new File(htmlPath,spuId+".html");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        PrintWriter printWriter = null;

        try {
            printWriter = new PrintWriter(file,"UTF-8");
            templateEngine.process("item",context,printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }finally {
            if(null != printWriter){
                printWriter.close();
            }
        }


        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> initStaticHTMLTemplate() {
        this.operationStaticHTML(CREATE_STATIC_HTML);

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> clearStaticHTMLTemplate() {
        this.operationStaticHTML(DELETE_STATIC_HTML);

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> deleteStaticHTMLTemplate(Integer spuId) {

        File file = new File(htmlPath,spuId + ".html");
            if(file.exists()){
                file.delete();
            }
        return this.setResultSuccess();
    }



    private Boolean operationStaticHTML(Integer operation){
        try {
            Result<List<SpuDTO>> spuInfo = goodsFeign.list(new SpuDTO());
            if (spuInfo.isSuccess()){
                spuInfo.getData().stream().forEach(spuDTO ->{
                    if(operation == 1){
                        this.createStaticHTMLTemplate(spuDTO.getId());
                    }else{
                        this.deleteStaticHTMLTemplate(spuDTO.getId());
                    }
                });
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private Map<String, Object> getGoodsInfo(Integer spuId) {

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
        goodsMap.put("spuDetail",this.getSpuDetail(spuId));

        //分类信息
        goodsMap.put("categoryInfo",this.getCategoryInfo(spuDTO1.getCid1() + "", spuDTO1.getCid2() + ""
                , spuDTO1.getCid3() + ""));
        //品牌信息
        goodsMap.put("brandInfo", this.getBrandInfo(spuDTO1.getBrandId()));
        //sku
        goodsMap.put("skus",this.getSkus(spuId));

        //规格组,规格参数(通用)
        goodsMap.put("specGroupAndParam",this.getSpecGroupAndParamResult(spuDTO.getCid3()));

        //特殊规格
        goodsMap.put("specParamMap",this.getSpecParamMap(spuDTO.getCid3()));

        return goodsMap;
    }
    //查询spu


    //在查询spuDetail
    private SpuDetailEntity getSpuDetail(Integer spuId){
        Result<SpuDetailEntity> spuDetailResult = goodsFeign.spuDetailList(spuId);
        SpuDetailEntity data = null;
        if(spuDetailResult.isSuccess()){
            data = spuDetailResult.getData();

        }
        return data;
    }

    //分类信息
    private List<CategoryEntity> getCategoryInfo(String cid1,String cid2,String cid3){
        Result<List<CategoryEntity>> categoryResult = categoryFeign.getCategoryByid(
                String.join(","
                        , Arrays.asList(cid1,cid2,cid3))
        );
        List<CategoryEntity> data = null;
        if(categoryResult.isSuccess()){
             data = categoryResult.getData();

        }
        return data;
    }


    //品牌信息
    private BrandEntity getBrandInfo( Integer brandId){
        BrandDTO brandDTO = new BrandDTO();
        brandDTO.setId(brandId);
        Result<PageInfo<BrandEntity>> brandRreslt = brandFeign.list(brandDTO);
        BrandEntity brandEntity = null;
        if (brandRreslt.isSuccess()){
             brandEntity = brandRreslt.getData().getList().get(0);


        }
        return brandEntity;
    }

    //sku
    private List<SkuDTO> getSkus(Integer spuId){
        Result<List<SkuDTO>> spuSkuResult = goodsFeign.getSpuSkuByIdList(spuId);
        List<SkuDTO> skuDTOdata = null;
        if (spuSkuResult.isSuccess()){
             skuDTOdata = spuSkuResult.getData();
        }
        return skuDTOdata;
    }

    //规格组,规格参数(通用)
    private List<SpecGroupDTO> getSpecGroupAndParamResult(Integer cid3){
        SpecGroupDTO specGroupDTO = new SpecGroupDTO();
        specGroupDTO.setId(cid3);
        Result<List<SpecificationEntity>> specGroupResult = specificationFeign.getSpecList(specGroupDTO);
        List<SpecGroupDTO> specGroupAndParamResult = null;
        if(specGroupResult.isSuccess()){

            List<SpecificationEntity> specGroupList = specGroupResult.getData();
            specGroupAndParamResult = specGroupList.stream().map(specGroup -> {
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


        }
        return specGroupAndParamResult;
    }


    //特殊规格参数
    private Map<Integer, String> getSpecParamMap(Integer cid3){
        SpecParamDTO specParamDTO = new SpecParamDTO();

        specParamDTO.setCid(cid3);
        specParamDTO.setGeneric(false);
        Result<List<SpecParamEntity>> specParamResult = specParamFeign.list(specParamDTO);
        Map<Integer, String> specParamMap = new HashMap<>();
        if(specParamResult.isSuccess()){
            List<SpecParamEntity> specParamResultData = specParamResult.getData();

            specParamResultData.stream().forEach(specParam -> specParamMap.put(
                    specParam.getId(),specParam.getName())
            );

        }
        return specParamMap;
    }
}
