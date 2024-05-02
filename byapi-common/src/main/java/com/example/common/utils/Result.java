package com.example.common.utils;

import lombok.Data;

/**
 * 返回结果工具类
 *
 * @author by
 */
@Data
public class Result<T> {

    /**
     * 响应码
     */
    private Integer code;
    /**
     * 响应数据
     */
    private T data;
    /**
     * 响应信息
     */
    private String message;

    /**
     * 响应成功，不带数据
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setData(null);
        result.setMessage("请求成功");
        return result;
    }

    /**
     * 响应成功，带返回数据
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setData(data);
        result.setMessage("请求成功");
        return result;
    }

    /**
     * 响应失败
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setData(null);
        result.setMessage(message);
        return result;
    }
}
