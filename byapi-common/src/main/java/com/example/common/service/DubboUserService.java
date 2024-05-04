package com.example.common.service;

import com.example.common.model.entity.User;

/**
 * @author by
 */
public interface DubboUserService {

    /**
     * 根据accessKey获取用户信息
     *
     * @param accessKey 签名
     * @return 用户信息
     */
    public User getInvokeUser(String accessKey);
}
