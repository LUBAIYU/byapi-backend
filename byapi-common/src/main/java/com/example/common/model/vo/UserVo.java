package com.example.common.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author by
 */
@Data
public class UserVo {
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
     * 邮箱
     */
    private String email;

    /**
     * 性别（0-男，1-女）
     */
    private Integer gender;

    /**
     * 用户角色：user / admin
     */
    private String userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
