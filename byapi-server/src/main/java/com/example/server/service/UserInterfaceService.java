package com.example.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.model.entity.UserInterfaceInfo;

/**
 * @author by
 */
public interface UserInterfaceService extends IService<UserInterfaceInfo> {

    /**
     * 统计接口调用次数
     *
     * @param interfaceInfoId 接口ID
     * @param userId          用户ID
     * @return 布尔值
     */
    public boolean invokeCount(long interfaceInfoId, long userId);
}
