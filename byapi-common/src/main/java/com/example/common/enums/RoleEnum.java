package com.example.common.enums;

import lombok.Getter;

/**
 * 用户角色枚举
 *
 * @author by
 */
@Getter
public enum RoleEnum {

    /**
     * 管理员
     */
    ADMIN("admin"),
    /**
     * 普通用户
     */
    USER("user");

    private final String role;

    RoleEnum(String role) {
        this.role = role;
    }
}
