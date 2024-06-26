package com.example.server.controller;

import cn.hutool.core.util.StrUtil;
import com.example.common.annotation.LoginCheck;
import com.example.common.annotation.MustAdmin;
import com.example.common.constant.UserConsts;
import com.example.common.enums.ErrorCode;
import com.example.common.exception.BusinessException;
import com.example.common.model.dto.EmailDto;
import com.example.common.model.dto.LoginDto;
import com.example.common.model.dto.RegisterDto;
import com.example.common.model.dto.UserPageDto;
import com.example.common.model.dto.UserUpdateDto;
import com.example.common.model.entity.User;
import com.example.common.model.vo.KeyVo;
import com.example.common.model.vo.UserVo;
import com.example.common.utils.PageBean;
import com.example.common.utils.Result;
import com.example.server.service.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author by
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public Result<UserVo> userLogin(@RequestBody LoginDto loginDto, HttpServletRequest request) {
        if (loginDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVo userVo = userService.userLogin(loginDto, request);
        return Result.success(userVo);
    }

    @PostMapping("/email/login")
    public Result<UserVo> emailLogin(@RequestBody EmailDto emailDto, HttpServletRequest request) {
        if (emailDto == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVo userVo = userService.emailLogin(emailDto, request);
        return Result.success(userVo);
    }

    @PostMapping("/logout")
    @LoginCheck
    public Result<Void> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        request.getSession().setAttribute(UserConsts.USER_LOGIN_STATE, null);
        return Result.success();
    }

    @GetMapping("/get/loginUser")
    public Result<UserVo> getLoginUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVo userVo = userService.getLoginUser(request);
        return Result.success(userVo);
    }

    @PostMapping("/register")
    public Result<Void> userRegister(@RequestBody RegisterDto registerDto) {
        if (registerDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        userService.userRegister(registerDto);
        return Result.success();
    }

    @PostMapping("/email/register")
    public Result<Void> emailRegister(@RequestBody EmailDto emailDto) {
        if (emailDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        userService.emailRegister(emailDto);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    @MustAdmin
    public Result<Void> deleteUser(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        userService.removeById(id);
        return Result.success();
    }

    @PutMapping("/update")
    @LoginCheck
    public Result<Void> updateUser(@RequestBody UserUpdateDto userUpdateDto, HttpServletRequest request) {
        if (userUpdateDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        userService.updateUser(userUpdateDto, request);
        return Result.success();
    }

    @GetMapping("/page")
    @MustAdmin
    public Result<PageBean<User>> listUsersByPage(UserPageDto userPageDto) {
        if (userPageDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        PageBean<User> pageBean = userService.listUsersByPage(userPageDto);
        return Result.success(pageBean);
    }

    @PutMapping("/alter/status")
    @MustAdmin
    public Result<Void> alterStatus(Long id, Integer status) {
        if (id == null || status == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        userService.alterStatus(id, status);
        return Result.success();
    }

    @PostMapping("/upload/avatar")
    @LoginCheck
    public Result<String> uploadAvatar(MultipartFile multipartFile) {
        if (multipartFile == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String imageUrl = userService.uploadAvatar(multipartFile);
        return Result.success(imageUrl);
    }

    @GetMapping("/get/avatar/{fileName}")
    @LoginCheck
    public void getAvatar(@PathVariable String fileName, HttpServletResponse response) {
        if (StrUtil.isBlank(fileName) || response == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        userService.getAvatar(fileName, response);
    }

    @PostMapping("/apply/key")
    @LoginCheck
    public Result<KeyVo> applyKey(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        KeyVo keyVo = userService.applyKey(request);
        return Result.success(keyVo);
    }

    @PostMapping("/mail/send")
    public Result<Void> sendMail(String email, HttpServletRequest request) {
        if (email == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        userService.sendEmail(email, request);
        return Result.success();
    }

    @GetMapping("/get/key")
    @LoginCheck
    public Result<KeyVo> getKeyById(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        KeyVo keyVo = userService.getKeyById(request);
        return Result.success(keyVo);
    }

    @GetMapping("/download/jar")
    @LoginCheck
    public void downloadJar(HttpServletResponse response) {
        if (response == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        userService.downloadJar(response);
    }
}
