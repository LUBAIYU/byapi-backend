package com.example.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.model.dto.InterfaceAddDto;
import com.example.common.model.dto.InterfaceInvokeDto;
import com.example.common.model.dto.InterfacePageDto;
import com.example.common.model.dto.InterfaceUpdateDto;
import com.example.common.model.entity.InterfaceInfo;
import com.example.common.utils.PageBean;

import javax.servlet.http.HttpServletRequest;

/**
 * @author by
 */
public interface InterfaceService extends IService<InterfaceInfo> {

    /**
     * 添加接口
     *
     * @param interfaceAddDto 添加接口请求体
     */
    void addInterface(InterfaceAddDto interfaceAddDto);

    /**
     * 根据ID删除接口
     *
     * @param id 接口ID
     */
    void deleteInterface(Long id);

    /**
     * 更新接口
     *
     * @param interfaceUpdateDto 更新接口请求体
     */
    void updateInterface(InterfaceUpdateDto interfaceUpdateDto);

    /**
     * 分页查询接口
     *
     * @param interfacePageDto 接口分页请求体
     * @return 接口分页响应体
     */
    PageBean<InterfaceInfo> listInterfacesByPage(InterfacePageDto interfacePageDto);

    /**
     * 更改接口状态
     *
     * @param id     接口ID
     * @param status 状态
     */
    void alterStatus(Long id, Integer status);

    /**
     * 根据ID调用接口
     *
     * @param interfaceInvokeDto 接口调用请求体
     * @param request            请求对象
     * @return 调用结果
     */
    Object invokeInterface(InterfaceInvokeDto interfaceInvokeDto, HttpServletRequest request);
}
