package com.example.common.service;

/**
 * @author by
 */
public interface DubboUserInterfaceService {

    /**
     * 统计接口调用次数
     *
     * @param interfaceInfoId 接口ID
     * @param userId          用户ID
     * @return 布尔值
     */
    public boolean invokeCount(long interfaceInfoId, long userId);
}
