package com.example.server.service;

import com.example.common.model.dto.EmailDto;
import com.example.common.model.dto.LoginDto;
import com.example.common.model.dto.RegisterDto;
import com.example.common.model.dto.UserPageDto;
import com.example.common.model.dto.UserUpdateDto;
import com.example.common.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.model.vo.KeyVo;
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
     * @param request       请求对象
     */
    void updateUser(UserUpdateDto userUpdateDto, HttpServletRequest request);

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

    /**
     * 给指定用户分发密钥
     *
     * @param request 请求对象
     * @return 密钥对
     */
    KeyVo applyKey(HttpServletRequest request);

    /**
     * 发送邮件
     *
     * @param mail    指定邮箱
     * @param request 请求对象
     */
    void sendEmail(String mail, HttpServletRequest request);

    /**
     * 邮箱登录
     *
     * @param emailDto 邮箱登录请求体
     * @param request  请求对象
     * @return 登录用户信息
     */
    UserVo emailLogin(EmailDto emailDto, HttpServletRequest request);

    /**
     * 邮箱注册
     *
     * @param emailDto 邮箱注册请求体
     */
    void emailRegister(EmailDto emailDto);

    /**
     * 根据ID获取用户的密钥
     *
     * @param request 请求对象
     * @return 密钥
     */
    KeyVo getKeyById(HttpServletRequest request);
}
