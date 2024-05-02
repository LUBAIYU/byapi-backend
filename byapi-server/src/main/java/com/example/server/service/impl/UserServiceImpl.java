package com.example.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.constant.CommonConsts;
import com.example.common.constant.UserConsts;
import com.example.common.enums.ErrorCode;
import com.example.common.enums.RoleEnum;
import com.example.common.exception.BusinessException;
import com.example.common.model.dto.LoginDto;
import com.example.common.model.dto.RegisterDto;
import com.example.common.model.dto.UserPageDto;
import com.example.common.model.dto.UserUpdateDto;
import com.example.common.model.entity.User;
import com.example.common.model.vo.UserVo;
import com.example.common.utils.PageBean;
import com.example.server.mapper.UserMapper;
import com.example.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

/**
 * @author by
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Value("${byapi.server.path.domain}")
    private String domain;
    @Value("${byapi.server.path.address}")
    private String address;

    @Override
    public UserVo userLogin(LoginDto loginDto, HttpServletRequest request) {
        String userAccount = loginDto.getUserAccount();
        String userPassword = loginDto.getUserPassword();
        //判断参数是否合法
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //账号长度不能小于4位
        if (userAccount.length() < UserConsts.USER_NAME_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, UserConsts.USER_NAME_ERROR);
        }
        //密码长度不能小于8位
        if (userPassword.length() < UserConsts.USER_PASSWORD_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, UserConsts.USER_PASSWORD_ERROR);
        }
        //判断用户是否存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserAccount, userAccount);
        User user = this.getOne(wrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, UserConsts.USER_PARAMS_ERROR);
        }
        //判断密码是否正确
        String encryptPassword = DigestUtil.md5Hex(userPassword + user.getSalt());
        if (!user.getUserPassword().equals(encryptPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, UserConsts.USER_PARAMS_ERROR);
        }
        //用户信息脱敏
        UserVo userVo = new UserVo();
        BeanUtil.copyProperties(user, userVo);
        //设置用户登录态
        request.getSession().setAttribute(UserConsts.USER_LOGIN_STATE, userVo);
        //返回
        return userVo;
    }

    @Override
    public void userRegister(RegisterDto registerDto) {
        String userAccount = registerDto.getUserAccount();
        String userPassword = registerDto.getUserPassword();
        String confirmPassword = registerDto.getConfirmPassword();
        //判断参数是否合法
        if (StringUtils.isAnyBlank(userAccount, userPassword, confirmPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断参数长度是否合法
        if (userAccount.length() < UserConsts.USER_NAME_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, UserConsts.USER_NAME_ERROR);
        }
        if (userPassword.length() < UserConsts.USER_PASSWORD_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, UserConsts.USER_PASSWORD_ERROR);
        }
        //判断确认密码和密码是否一致
        if (!userPassword.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, UserConsts.PASSWORD_NOT_EQUAL);
        }
        //判断用户名是否存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserName, userAccount);
        User user = this.getOne(wrapper);
        if (user != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, UserConsts.USER_NAME_EXIST);
        }
        //生成一个随机的盐
        String salt = RandomUtil.randomString(4);
        //对密码进行加密
        String encryptPassword = DigestUtil.md5Hex(userPassword + salt);
        //插入用户数据
        user = new User();
        user.setUserName(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserRole(RoleEnum.USER.getRole());
        user.setSalt(salt);
        this.save(user);
    }

    @Override
    public UserVo getLoginUser(HttpServletRequest request) {
        //获取用户信息
        Object object = request.getSession().getAttribute(UserConsts.USER_LOGIN_STATE);
        UserVo userVo = (UserVo) object;
        if (userVo == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return userVo;
    }

    @Override
    public void updateUser(UserUpdateDto userUpdateDto) {
        Long id = userUpdateDto.getId();
        String userAccount = userUpdateDto.getUserAccount();
        Integer status = userUpdateDto.getStatus();
        String userRole = userUpdateDto.getUserRole();
        //判断部分参数是否合法
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isAnyBlank(userAccount, userRole)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (status == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //更新
        User user = new User();
        BeanUtil.copyProperties(userUpdateDto, user);
        this.updateById(user);
    }

    @Override
    public PageBean<User> listUsersByPage(UserPageDto userPageDto) {
        Long id = userPageDto.getId();
        String userName = userPageDto.getUserName();
        String userAccount = userPageDto.getUserAccount();
        Integer gender = userPageDto.getGender();
        Integer status = userPageDto.getStatus();
        Integer current = userPageDto.getCurrent();
        Integer pageSize = userPageDto.getPageSize();
        if (current == null || current <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, CommonConsts.PAGE_PARAMS_ERROR);
        }
        if (pageSize == null || pageSize < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, CommonConsts.PAGE_PARAMS_ERROR);
        }
        //添加分页条件
        Page<User> page = new Page<>(current, pageSize);
        //添加查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(id != null, User::getId, userPageDto.getId());
        wrapper.like(StrUtil.isNotBlank(userName), User::getUserName, userPageDto.getUserName());
        wrapper.like(StrUtil.isNotBlank(userAccount), User::getUserAccount, userPageDto.getUserAccount());
        wrapper.eq(gender != null, User::getGender, userPageDto.getGender());
        wrapper.eq(status != null, User::getStatus, userPageDto.getStatus());
        //查询
        this.page(page, wrapper);
        //获取数据
        long total = page.getTotal();
        List<User> records = page.getRecords();
        return PageBean.of(total, records);
    }

    @Override
    public void alterStatus(Long id, Integer status) {
        //判断参数是否合法
        if (id <= 0 || status < 0 || status > 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //获取用户数据
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //判断状态是否一样
        if (user.getStatus().equals(status)) {
            return;
        }
        //更新状态
        user.setStatus(status);
        this.updateById(user);
    }

    @Override
    public String uploadAvatar(MultipartFile multipartFile) {
        //判断文件名是否为空
        String originalFilename = multipartFile.getOriginalFilename();
        if (StrUtil.isBlank(originalFilename)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断图片后缀是否存在
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (StrUtil.isBlank(suffix)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, CommonConsts.IMAGE_FORMAT_ERROR);
        }
        //生成随机文件名
        String newFileName = UUID.randomUUID().toString().replace("-", "") + suffix;
        //上传图片
        File dest = new File(address + "/" + newFileName);
        try {
            multipartFile.transferTo(dest);
        } catch (Exception e) {
            log.error("图片上传失败", e);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, CommonConsts.IMAGE_UPLOAD_ERROR);
        }
        //获取并返回图片请求路径
        return domain + "/user/get/avatar/" + newFileName;
    }

    @Override
    public void getAvatar(String fileName, HttpServletResponse response) {
        //获取文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        //获取图片存放路径
        String url = address + "/" + fileName;
        //响应图片
        response.setContentType("image/" + suffix);
        //从服务器中读取图片
        try (
                //获取输出流
                OutputStream outputStream = response.getOutputStream();
                //获取输入流
                FileInputStream fileInputStream = new FileInputStream(url)
        ) {
            byte[] buffer = new byte[1024];
            int b;
            while ((b = fileInputStream.read(buffer)) != -1) {
                //将图片以字节流形式写入输出流
                outputStream.write(buffer, 0, b);
            }
        } catch (IOException e) {
            log.error("文件读取失败", e);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, CommonConsts.IMAGE_READ_ERROR);
        }
    }
}




