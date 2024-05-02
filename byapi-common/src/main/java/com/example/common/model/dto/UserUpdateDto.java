package com.example.common.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新用户请求体
 *
 * @author by
 */
@Data
public class UserUpdateDto implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 性别（0-男，1-女）
     */
    private Integer gender;

    /**
     * 用户状态（0-启用，1-禁用）
     */
    private Integer status;

    /**
     * 用户角色：user / admin
     */
    private String userRole;
}
