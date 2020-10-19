package com.baidu.shop.dto;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName UserDTO
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/16
 * @Version V1.0
 **/
@Data
public class UserDTO {

    private Integer id;

    private String username;

    private String password;

    private String phone;

    private Date created;

    private String salt;

    private Integer type;
}
