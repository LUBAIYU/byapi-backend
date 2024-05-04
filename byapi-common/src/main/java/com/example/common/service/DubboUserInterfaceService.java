package com.example.common.service;

/**
 * @author by
 */
public interface DubboUserInterfaceService {

    /**
     * 统计接口调用次数
     *
     * @param interfaceId 接口ID
     * @param userId      用户ID
     */
    void invokeCount(long interfaceId, long userId);
}
