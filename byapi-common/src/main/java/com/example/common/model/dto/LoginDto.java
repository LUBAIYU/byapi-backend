package com.example.common.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求体
 *
 * @author by
 */
@Data
public class LoginDto implements Serializable {

    /**
     * 账号
     */
    private String userAccount;
    /**
     * 密码
     */
    private String userPassword;
}
