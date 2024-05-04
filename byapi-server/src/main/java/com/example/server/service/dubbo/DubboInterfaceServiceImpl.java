package com.example.server.service.dubbo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.enums.ErrorCode;
import com.example.common.exception.BusinessException;
import com.example.common.model.entity.InterfaceInfo;
import com.example.common.service.DubboInterfaceService;
import com.example.server.service.InterfaceService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author by
 */
@DubboService
public class DubboInterfaceServiceImpl implements DubboInterfaceService {

    @Resource
    private InterfaceService interfaceService;

    /**
     * 根据url和method获取接口信息
     *
     * @param url    接口地址
     * @param method 接口方法
     * @return 接口信息
     */
    @Override
    public InterfaceInfo getInterfaceInfo(String url, String method) {
        if (StringUtils.isAnyBlank(url, method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<InterfaceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InterfaceInfo::getUrl, url);
        wrapper.eq(InterfaceInfo::getMethod, method);
        return interfaceService.getOne(wrapper);
    }
}
