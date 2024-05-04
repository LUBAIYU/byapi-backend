package com.example.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.model.dto.UserInterfacePageDto;
import com.example.common.model.dto.UserInterfaceUpdateDto;
import com.example.common.model.entity.UserInterfaceInfo;
import com.example.common.utils.PageBean;

import javax.servlet.http.HttpServletRequest;

/**
 * @author by
 */
public interface UserInterfaceService extends IService<UserInterfaceInfo> {

    /**
     * 统计接口调用次数
     *
     * @param interfaceId 接口ID
     * @param userId      用户ID
     */
    void invokeCount(long interfaceId, long userId);

    /**
     * 添加用户接口关联信息
     *
     * @param interfaceId 接口ID
     * @param request     请求对象
     */
    void addUserInterface(Long interfaceId, HttpServletRequest request);

    /**
     * 更新用户接口关联信息
     *
     * @param userInterfaceUpdateDto 用户接口信息请体
     */
    void updateUserInterface(UserInterfaceUpdateDto userInterfaceUpdateDto);

    /**
     * 条件分页查询
     *
     * @param userInterfacePageDto 查询请求体
     * @return 查询数据
     */
    PageBean<UserInterfaceInfo> pageUserInterfaces(UserInterfacePageDto userInterfacePageDto);
}
