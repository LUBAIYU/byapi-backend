package com.example.common.enums;

import lombok.Getter;

/**
 * 错误信息枚举类
 *
 * @author by
 */
@Getter
public enum ErrorCode {

    /**
     * 基本错误信息枚举
     */
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    SYSTEM_ERROR(50000, "系统内部异常");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 错误信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
