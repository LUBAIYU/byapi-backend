package com.example.common.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 邮箱登录请求体
 *
 * @author by
 */
@Data
public class EmailDto implements Serializable {
    /**
     * 邮箱
     */
    private String email;
    /**
     * 验证码
     */
    private String verCode;
}
