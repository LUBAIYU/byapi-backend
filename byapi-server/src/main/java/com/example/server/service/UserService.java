package com.example.server.service;

import com.example.common.model.dto.LoginDto;
import com.example.common.model.dto.RegisterDto;
import com.example.common.model.dto.UserPageDto;
import com.example.common.model.dto.UserUpdateDto;
import com.example.common.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.model.vo.UserVo;
import com.example.common.utils.PageBean;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author by
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     *
     * @param loginDto 登录请求体
     * @param request  请求对象
     * @return 脱敏后的用户信息
     */
    UserVo userLogin(LoginDto loginDto, HttpServletRequest request);

    /**
     * 用户注册
     *
     * @param registerDto 注册请求体
     */
    void userRegister(RegisterDto registerDto);

    /**
     * 获取当前登录用户信息
     *
     * @param request 请求对象
     * @return 返回用户信息
     */
    UserVo getLoginUser(HttpServletRequest request);

    /**
     * 更新用户信息
     *
     * @param userUpdateDto 用户更新信息
     */
    void updateUser(UserUpdateDto userUpdateDto);

    /**
     * 分页条件查询
     *
     * @param userPageDto 查询请求体
     * @return 用户数据
     */
    PageBean<User> listUsersByPage(UserPageDto userPageDto);

    /**
     * 修改用户状态
     *
     * @param id     用户ID
     * @param status 用户状态
     */
    void alterStatus(Long id, Integer status);

    /**
     * 上传头像
     *
     * @param multipartFile 文件
     * @return 头像地址
     */
    String uploadAvatar(MultipartFile multipartFile);

    /**
     * 获取头像
     *
     * @param fileName 文件名
     * @param response 响应对象
     */
    void getAvatar(String fileName, HttpServletResponse response);
}
