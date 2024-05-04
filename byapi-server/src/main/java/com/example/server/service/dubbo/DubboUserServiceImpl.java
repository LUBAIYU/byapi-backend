package com.example.server.service.dubbo;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.enums.ErrorCode;
import com.example.common.exception.BusinessException;
import com.example.common.model.entity.User;
import com.example.common.service.DubboUserService;
import com.example.server.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author by
 */
@DubboService
public class DubboUserServiceImpl implements DubboUserService {

    @Resource
    private UserService userService;

    @Override
    public User getInvokeUser(String accessKey) {
        if (StrUtil.isBlank(accessKey)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getAccessKey, accessKey);
        return userService.getOne(wrapper);
    }
}
