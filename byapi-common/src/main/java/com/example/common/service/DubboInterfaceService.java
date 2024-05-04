package com.example.common.service;

import com.example.common.model.entity.InterfaceInfo;

/**
 * @author by
 */
public interface DubboInterfaceService {

    /**
     * 根据url和method获取接口信息
     *
     * @param url    接口地址
     * @param method 接口方法
     * @return 接口信息
     */
    public InterfaceInfo getInterfaceInfo(String url, String method);
}
