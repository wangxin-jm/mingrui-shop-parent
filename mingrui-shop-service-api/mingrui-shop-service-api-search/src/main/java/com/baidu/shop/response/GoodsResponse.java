package com.baidu.shop.response;

import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.status.HTTPStatus;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 2 * @ClassName GoodsResponse
 * 3 * @Description: TODO
 * 4 * @Author wangxin
 * 5 * @Date 2021/3/6
 * 6 * @Version V1.0
 * 7
 **/
@Data
public class GoodsResponse extends Result<List<GoodsDoc>> {

    private Long total;

    private Long totalPage;

    private List<CategoryEntity> categoryList;

    private List<BrandEntity> brandList;

    private Map<String, List<String>> specMap;

    public GoodsResponse (Long total,Long totalPage,List<CategoryEntity> categoryList
            , List<BrandEntity> brandList,List<GoodsDoc> goodsDocList,Map<String, List<String>> specMap){

        super(HTTPStatus.OK,"",goodsDocList);
        this.total = total;
        this.totalPage = totalPage;
        this.categoryList = categoryList;
        this.brandList = brandList;
        this.specMap = specMap;


    }

}
