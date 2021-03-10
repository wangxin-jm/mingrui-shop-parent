package com.baidu.controller;

import com.baidu.service.PageService;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 2 * @ClassName PageController
 * 3 * @Description: TODO
 * 4 * @Author wangxin
 * 5 * @Date 2021/3/8
 * 6 * @Version V1.0
 * 7
 **/
//@Controller
//@RequestMapping(value = "item")
public class PageController {

//    @Autowired
    private PageService pageService;

//    @GetMapping(value = "{spuId}.html")
    public String test(@PathVariable(value="spuId") Integer spuId, ModelMap modelMap){
        Map<String,Object> map = pageService.getGoodsInfo(spuId);
        modelMap.putAll(map);
        return "item";

    }


}
