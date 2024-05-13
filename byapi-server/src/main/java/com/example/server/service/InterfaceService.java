package com.example.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.model.dto.InterfaceAddDto;
import com.example.common.model.dto.InterfaceInvokeDto;
import com.example.common.model.dto.InterfacePageDto;
import com.example.common.model.dto.InterfaceUpdateDto;
import com.example.common.model.entity.InterfaceInfo;
import com.example.common.model.vo.InterfaceVo;
import com.example.common.utils.PageBean;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    /**
     * 开通接口权限
     *
     * @param interfaceId 接口ID
     * @param request     请求对象
     */
    void openPermission(Long interfaceId, HttpServletRequest request);

    /**
     * 根据ID获取接口信息
     *
     * @param id      接口ID
     * @param request 请求对象
     * @return 接口信息
     */
    InterfaceVo getInterfaceById(Long id, HttpServletRequest request);

    /**
     * 获取接口调用记录
     *
     * @param request 请求对象
     * @return 记录
     */
    List<InterfaceVo> listInvokeRecords(HttpServletRequest request);

    /**
     * 根据接口ID获取代码示例
     *
     * @param interfaceId 接口ID
     * @return 代码示例
     */
    String getCodeExample(Long interfaceId);
}
