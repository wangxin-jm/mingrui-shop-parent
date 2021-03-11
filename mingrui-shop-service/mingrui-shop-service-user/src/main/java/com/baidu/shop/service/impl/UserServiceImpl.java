package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.mapper.UserMapper;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.service.UserService;
import com.baidu.shop.utils.BCryptUtil;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 2 * @ClassName UserServiceImpl
 * 3 * @Description: TODO
 * 4 * @Author wangxin
 * 5 * @Date 2021/3/10
 * 6 * @Version V1.0
 * 7
 **/
@RestController
@Slf4j
public class UserServiceImpl extends BaseApiService implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisRepository redisRepository;


    @Override
    public Result<JSONObject> yanzhengma(String phone1, String code1) {
        String s = redisRepository.get(RedisUtil.CODE_YANZHENGMA + phone1);
        if(!code1.equals(s)){
            return this.setResultError("验证码输入错误");
        }


        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> register(UserDTO userDTO) {
        UserEntity userEntity = BaiduBeanUtil.beanUtil(userDTO,UserEntity.class);
        userEntity.setPassword(BCryptUtil.hashpw(userEntity.getPassword(),BCryptUtil.gensalt()));
        userEntity.setCreated(new Date());
        userMapper.insertSelective(userEntity);
        return this.setResultSuccess();
    }

    @Override
    public Result<List<UserEntity>> checkUserNameOrPhone(String value, Integer type) {

        Example example = new Example(UserEntity.class);
        Example.Criteria criteria = example.createCriteria();

        if(type != null && value != null){
            if(type == 1){
                //通过用户名效验
                criteria.andEqualTo("username",value);
            }else{
                //通过手机号效验
                criteria.andEqualTo("phone",value);
            }
        }

        List<UserEntity> userEntities = userMapper.selectByExample(example);


        return this.setResultSuccess(userEntities);
    }

    @Override
    public Result<JSONObject> sendValidCode(UserDTO userDTO) {
        //生成随机6位验证码
        String code = (int)((Math.random() * 9 + 1) * 100000) + "";
        //发送短信验证码
//        System.out.println(code);
//        短信验证
//        LuosimaoDuanxinUtil.SendCode(userDTO.getPhone(),code);
//        语音验证
//        LuosimaoDuanxinUtil.sendSpeak(userDTO.getPhone(),code);
        log.debug("手机号是:{},验证码是:{}",userDTO.getPhone(),code);
//
        redisRepository.set(RedisUtil.CODE_YANZHENGMA + userDTO.getPhone(),code);
//
        redisRepository.expire(RedisUtil.CODE_YANZHENGMA + userDTO.getPhone(),60L);

        return this.setResultSuccess();
    }
}
