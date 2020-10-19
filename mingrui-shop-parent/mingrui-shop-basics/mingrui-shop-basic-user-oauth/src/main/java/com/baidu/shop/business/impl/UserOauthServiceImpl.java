package com.baidu.shop.business.impl;

import com.baidu.shop.business.UserOauthService;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.constant.UserConstant;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.mapper.UserOauthMapper;
import com.baidu.shop.util.BCryptUtil;
import com.baidu.shop.util.BaiduBeanUtil;
import com.baidu.shop.util.JwtUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName UserOauthServiceImpl
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/15
 * @Version V1.0
 **/
@Service
public class UserOauthServiceImpl implements UserOauthService {

    @Resource
    private UserOauthMapper userOauthMapper;

    @Override
    public String checkUser(UserDTO userDTO, JwtConfig jwtConfig) {

        String token = null;

        Example example = new Example(UserEntity.class);
        Example.Criteria criteria = example.createCriteria();

        if(UserConstant.USER_TYPE_USERNAME == userDTO.getType()){
            criteria.andEqualTo("username", userDTO.getUsername());
            List<UserEntity> userList = userOauthMapper.selectByExample(example);
            if(userList.size() == 1){
                if(BCryptUtil.checkpw(userDTO.getPassword(),userList.get(0).getPassword())){
                    UserInfo userInfo = BaiduBeanUtil.copyProperties(userList.get(0), UserInfo.class);
                    try {
                        token = JwtUtils.generateToken(userInfo,jwtConfig.getPrivateKey(),jwtConfig.getExpire());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }else if(UserConstant.USER_TYPE_PHONE == userDTO.getType()){
            criteria.andEqualTo("phone", userDTO.getPhone());
            List<UserEntity> userList = userOauthMapper.selectByExample(example);
            if(userList.size() == 1){
                UserInfo userInfo = BaiduBeanUtil.copyProperties(userList.get(0), UserInfo.class);
                try {
                    token = JwtUtils.generateToken(userInfo,jwtConfig.getPrivateKey(),jwtConfig.getExpire());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return token;
    }

}
