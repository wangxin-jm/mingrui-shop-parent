package com.baidu.com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.com.baidu.shop.feign.BrandFeign;
import com.baidu.com.baidu.shop.feign.CategoryFeign;
import com.baidu.com.baidu.shop.feign.GoodsFeign;
import com.baidu.com.baidu.shop.feign.SpecificationFeign;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.response.GoodsResponse;
import com.baidu.shop.service.ShopElasticsearchService;
import com.baidu.shop.utils.ESHighLightUtil;
import com.baidu.shop.utils.JSONUtil;
import com.github.pagehelper.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 2 * @ClassName ShopElasticsearchServiceImpl
 * 3 * @Description: TODO
 * 4 * @Author wangxin
 * 5 * @Date 2021/3/4
 * 6 * @Version V1.0
 * 7
 **/
@RestController
public class ShopElasticsearchServiceImpl extends BaseApiService implements
        ShopElasticsearchService {

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



    @Override
    public GoodsResponse search(String search, Integer page,String filter) {

        //查询es库
        SearchHits<GoodsDoc> goodsDocs  = elasticsearchRestTemplate.search(this.getSearch(search,page,filter).build(), GoodsDoc.class);

        List<GoodsDoc> goodsDoc = ESHighLightUtil.getHighlightList(goodsDocs.getSearchHits());

        Aggregations aggregations = goodsDocs.getAggregations();

        //总条数
        long total = goodsDocs.getTotalHits();
        //分页个数
        long totalPage = Double.valueOf(Math.ceil(Double.valueOf(total) / 10)).longValue();

//        Integer hotCid = 0;
//        List<CategoryEntity> categoryList = null;
//
        Map<Integer, List<CategoryEntity>> map = this.getCategoryList(goodsDocs.getAggregations());

        Integer hotCid = 0;

        List<CategoryEntity> categoryList    = null;
        for(Map.Entry<Integer, List<CategoryEntity>> entry : map.entrySet()){
            hotCid = entry.getKey();
            categoryList = entry.getValue();
        }



        return new GoodsResponse(total,totalPage,categoryList,this.getBrandList(aggregations),goodsDoc,this.getSpecMap(hotCid,search));
    }

    @Override
    public Result<JSONObject> saveData(Integer spuId) {

        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);

        List<GoodsDoc> goodsDocs = this.esGoodsInfo(spuDTO);
        elasticsearchRestTemplate.save(goodsDocs.get(0));
        return this.setResultSuccess();

    }

    @Override
    public Result<JSONObject> delData(Integer spuId) {
        GoodsDoc goodsDoc = new GoodsDoc();
        goodsDoc.setId(spuId.longValue());

        elasticsearchRestTemplate.delete(goodsDoc);
        return this.setResultSuccess();
    }

    private Map<String,List<String>> getSpecMap(Integer hotCid,String search){
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(hotCid);
        specParamDTO.setSearching(true);
        Result<List<SpecParamEntity>> list = specificationFeign.list(specParamDTO);
            //定义一个map
        Map<String,List<String>> specMap = new HashMap<>();
        if (list.isSuccess()){
            List<SpecParamEntity> specParamList  = list.getData();
            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();


            nativeSearchQueryBuilder.withQuery(
                    QueryBuilders.multiMatchQuery(search,"title","brandName","categoryName"));
            nativeSearchQueryBuilder.withPageable(PageRequest.of(0,1));
            specParamList.stream().forEach(specParam -> {
                nativeSearchQueryBuilder.addAggregation(
                        AggregationBuilders.terms(specParam.getName())
                                .field("specs." + specParam.getName() + ".keyword"));
            });


            SearchHits<GoodsDoc> searchHits  = elasticsearchRestTemplate.search(nativeSearchQueryBuilder.build(), GoodsDoc.class);
            Aggregations aggregations = searchHits.getAggregations();

            specParamList.stream().forEach(specParam ->{
                Terms aggregation = aggregations.get(specParam.getName());
                List<? extends Terms.Bucket> buckets = aggregation.getBuckets();
                List<String> valueList  = buckets.stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
                specMap.put(specParam.getName(),valueList);
            });

        }

        return  specMap;
    }



    //得到NativeSearchQueryBuilder
    public  NativeSearchQueryBuilder  getSearch(String search,Integer page,String filter){
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        nativeSearchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","categoryName"));


        //条件过滤
        if( !StringUtil.isEmpty(filter) && filter.length() >2 ){

            //将字符创转换成map集合
            Map<String, String> stringStringMap = JSONUtil.toMapValueString(filter);
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            //遍历map集合
            stringStringMap.forEach((key,value) -> {

                MatchQueryBuilder matchQueryBuilder = null;
                //判断key是不是cid3还有brandId
                if(key.equals("brandId") || key.equals("cid3")){
                    matchQueryBuilder = QueryBuilders.matchQuery(key, value);
                }else{
                    matchQueryBuilder = QueryBuilders.matchQuery("specs." + key + ".keyword", value);
                }



                            boolQueryBuilder.must(matchQueryBuilder);

            });

            nativeSearchQueryBuilder.withFilter(boolQueryBuilder);


        }

        //设置分页
        nativeSearchQueryBuilder.withPageable(PageRequest.of(page-1,10));

        //降低压力不显示为null的
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","title","skus"},null));

        //设置高亮
        nativeSearchQueryBuilder.withHighlightBuilder(ESHighLightUtil.getHighlightBuilder("title"));

        //聚合
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("cate_agg").field("cid3"));
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("brand_agg").field("brandId"));
            return nativeSearchQueryBuilder;
    }


    //通过聚合得到品牌List
    public  List<BrandEntity>  getBrandList(Aggregations aggregations){
        Terms brandIdAgg = aggregations.get("brand_agg");
        //获取桶
        List<? extends Terms.Bucket> brandBuuckets = brandIdAgg.getBuckets();

        List<String> bardIdList = brandBuuckets.stream().map(brandBuucket ->
                brandBuucket.getKeyAsNumber().longValue()+ "").collect(Collectors.toList());

        Result<List<BrandEntity>> brandResult  = brandFeign.getBrandByIdList(String.join(",", bardIdList));

        List<BrandEntity> brandList = null;
        if (brandResult.isSuccess()) brandList= brandResult.getData();

        return brandList;


    }



    //通过聚合得到分类List
    public  Map<Integer, List<CategoryEntity>>  getCategoryList(Aggregations aggregations){

        Terms cid3Agg = aggregations.get("cate_agg");

        List<? extends Terms.Bucket> cateBuuckets = cid3Agg.getBuckets();

        List<Long> docCount = Arrays.asList(0L);
        List<Integer> hotCid = Arrays.asList(0);


        List<String> cateIdlist = cateBuuckets.stream().map(cateBuucket ->{
            if(cateBuucket.getDocCount() > docCount.get(0)){
                docCount.set(0,cateBuucket.getDocCount());
                hotCid.set(0,cateBuucket.getKeyAsNumber().intValue());

            }

               return  cateBuucket.getKeyAsNumber().longValue() + "";
        }).collect(Collectors.toList());

        Result<List<CategoryEntity>> categoryResult = categoryFeign.getCategoryByid(String.join(",", cateIdlist));

        List<CategoryEntity> categoryList = null;
        if (categoryResult.isSuccess())categoryList= categoryResult.getData();

        Map<Integer, List<CategoryEntity>> map = new HashMap<>();
            map.put(hotCid.get(0),categoryList);

        return map;

    }


    @Override
    public Result<JSONObject> initGoodsEsData() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        //查看索引是不是存在,!代表不存在 不存在就创建并且创建mapping
        if(!indexOperations.exists()){
            indexOperations.create();
            indexOperations.createMapping();
        }
        //获取全部数据
        List<GoodsDoc> goodsDocs = this.esGoodsInfo(new SpuDTO());
        //添加数据
        elasticsearchRestTemplate.save(goodsDocs);

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> clearGoodsEsData() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        //判断存不存在,存在就删除
        if(indexOperations.exists()){
            indexOperations.delete();
        }


        return this.setResultSuccess();
    }




    public List<GoodsDoc> esGoodsInfo(SpuDTO spuDTO ) {

//        SpuDTO spuDTO = new SpuDTO();


            Result<List<SpuDTO>> spuInfo = goodsFeign.list(spuDTO);

            if(spuInfo.isSuccess()){

                List<SpuDTO> spuList = spuInfo.getData();

                List<GoodsDoc> goodsDocList = spuList.stream().map(spu -> {
                    GoodsDoc goodsDoc = new GoodsDoc();
                    goodsDoc.setId(spu.getId().longValue());
                    goodsDoc.setTitle(spu.getTitle());
                    goodsDoc.setBrandName(spu.getBrandName());
                    goodsDoc.setCategoryName(spu.getCategoryName());
                    goodsDoc.setSubTitle(spu.getSubTitle());
                    goodsDoc.setBrandId(spu.getBrandId().longValue());
                    goodsDoc.setCid1(spu.getCid1().longValue());
                    goodsDoc.setCid2(spu.getCid2().longValue());
                    goodsDoc.setCid3(spu.getCid3().longValue());
                    goodsDoc.setCreateTime(spu.getCreateTime());

                    //通过spuid查询sku
                    Result<List<SkuDTO>> spuSkuByIdList = goodsFeign.getSpuSkuByIdList(spu.getId());
                    if (spuSkuByIdList.isSuccess()) {
                        // 一个spu所有商品价格集合
                        List<SkuDTO> skuList = spuSkuByIdList.getData();
                        //用来存储价格的集合
                        List<Long> priceList = new ArrayList<>();

                        List<Map<String, Object>> skuMapList = skuList.stream().map(sku -> {
//定义一个map集合,用来对方对立关系
                            Map<String, Object> map = new HashMap<>();
                            map.put("id", sku.getId());
                            map.put("title", sku.getTitle());
                            map.put("image", sku.getImages());
                            map.put("price", sku.getPrice());

                            //把价格存在集合中
                            priceList.add(sku.getPrice().longValue());

                            return map;
                        }).collect(Collectors.toList());

                        goodsDoc.setPrice(priceList);
                        goodsDoc.setSkus(JSONUtil.toJsonString(skuMapList));

                    }
                    //通过cid3查询查询规格参数,searching 1 为true
                    SpecParamDTO specParamDTO = new SpecParamDTO();

                    specParamDTO.setCid(spu.getCid3());
                    specParamDTO.setSearching(true);

                    Result<List<SpecParamEntity>> specParamInfo = specificationFeign.list(specParamDTO);

                    if (specParamInfo.isSuccess()) {
                        List<SpecParamEntity> specParamList =  specParamInfo.getData();//跟老师不一样
                        Result<SpuDetailEntity> spuDetailInfo = goodsFeign.spuDetailList(spu.getId());
                        if (spuDetailInfo.isSuccess()) {
                            SpuDetailEntity spuDetailEntity = spuDetailInfo.getData();
                            //将json字符串转换成map集合
                            Map<String, String> genericSpec = JSONUtil.toMapValueString(spuDetailEntity.getGenericSpec());
                            Map<String, List<String>> specialSpec = JSONUtil.toMapValueStrList(spuDetailEntity.getSpecialSpec());

                            //需要查询两张表的数据 spec_param(规格参数名) spu_detail(规格参数值) --> 规格参数名 : 规格参数值
                            Map<String, Object> specMap = new HashMap<>();

                            specParamList.stream().forEach(specParam -> {
                                if (specParam.getGeneric()) {//判断聪那个集合中获取的数据
                                    if (specParam.getNumeric() && !StringUtils.isEmpty(specParam.getSegments())) {

                                        specMap.put(specParam.getName(), chooseSegment(genericSpec.get(specParam.getId() + ""), specParam.getSegments(), specParam.getUnit()));
                                    } else {
                                        specMap.put(specParam.getName(), genericSpec.get(specParam.getId() + ""));
                                    }
                                } else {
                                    specMap.put(specParam.getName(), specialSpec.get(specParam.getId() + ""));
                                }
                            });
                            goodsDoc.setSpecs(specMap);
                        }
                    }

                    return goodsDoc;

                }).collect(Collectors.toList());

                System.out.println(goodsDocList);

                return goodsDocList;
            }

        return null;
    }

    private String chooseSegment(String value, String segments, String unit) {//800 -> 5000-1000
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : segments.split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + unit + "以上";
                }else if(begin == 0){
                    result = segs[1] + unit + "以下";
                }else{
                    result = segment + unit;
                }
                break;
            }
        }
        return result;
    }


}
