package com.example.server.service.inner;

import com.example.common.service.InnerUserInterfaceService;
import com.example.server.service.UserInterfaceService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * 对外暴露的Dubbo服务
 * @author by
 */
@DubboService
public class InnerUserInterfaceServiceImpl implements InnerUserInterfaceService {

    @Resource
    private UserInterfaceService userInterfaceService;

    /**
     * 统计接口调用次数
     *
     * @param interfaceInfoId 接口ID
     * @param userId          用户ID
     * @return 布尔值
     */
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        return userInterfaceService.invokeCount(interfaceInfoId, userId);
    }
}
