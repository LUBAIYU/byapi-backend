package com.example.common.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 注册请求体
 *
 * @author by
 */
@Data
public class RegisterDto implements Serializable {
    /**
     * 账号
     */
    private String userAccount;
    /**
     * 密码
     */
    private String userPassword;
    /**
     * 确认密码
     */
    private String confirmPassword;
}
