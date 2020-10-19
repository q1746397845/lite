package com.baidu.shop.business;

import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.entity.UserEntity;

/**
 * @ClassName UserOauthService
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/15
 * @Version V1.0
 **/
public interface UserOauthService {

    String checkUser(UserDTO userDTO, JwtConfig jwtConfig);
}
