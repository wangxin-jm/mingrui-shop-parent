package com.baidu.shop.utils;

import org.springframework.beans.BeanUtils;

/**
 * 2 * @ClassName BaiduBeanUtil
 * 3 * @Description: TODO
 * 4 * @Author wangxin
 * 5 * @Date 2020/12/25
 * 6 * @Version V1.0
 * 7
 **/
public class BaiduBeanUtil<T> {

    public static <T> T beanUtil(Object source,Class<T> clazz){
        try {
            //创建当前类的实例
            T  t =  clazz.newInstance();

            BeanUtils.copyProperties(source,t);

            return t;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }


}
