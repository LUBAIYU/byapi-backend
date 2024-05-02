package com.example.common.model.dto;

import com.example.common.utils.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分页请求体
 *
 * @author by
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserPageDto extends PageRequest {
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
     * 性别（0-男，1-女）
     */
    private Integer gender;

    /**
     * 用户状态（0-启用，1-禁用）
     */
    private Integer status;
}
