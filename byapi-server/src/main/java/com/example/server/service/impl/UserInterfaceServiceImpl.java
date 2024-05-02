package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.enums.ErrorCode;
import com.example.common.exception.BusinessException;
import com.example.common.model.entity.UserInterfaceInfo;
import com.example.server.mapper.UserInterfaceMapper;
import com.example.server.service.UserInterfaceService;
import org.springframework.stereotype.Service;

/**
 * @author by
 */
@Service
public class UserInterfaceServiceImpl extends ServiceImpl<UserInterfaceMapper, UserInterfaceInfo> implements UserInterfaceService {
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UpdateWrapper<UserInterfaceInfo> wrapper = new UpdateWrapper<>();
        wrapper.eq("interfaceInfo_id", interfaceInfoId);
        wrapper.eq("user_id", userId);
        wrapper.setSql("total_num=total_num + 1, left_num=left_num - 1");
        return this.update(wrapper);
    }
}
