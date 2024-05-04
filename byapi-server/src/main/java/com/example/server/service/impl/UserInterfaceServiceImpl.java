package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.constant.CommonConsts;
import com.example.common.enums.ErrorCode;
import com.example.common.exception.BusinessException;
import com.example.common.model.dto.UserInterfacePageDto;
import com.example.common.model.dto.UserInterfaceUpdateDto;
import com.example.common.model.entity.UserInterfaceInfo;
import com.example.common.model.vo.UserVo;
import com.example.common.utils.PageBean;
import com.example.server.mapper.UserInterfaceMapper;
import com.example.server.service.UserInterfaceService;
import com.example.server.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author by
 */
@Service
public class UserInterfaceServiceImpl extends ServiceImpl<UserInterfaceMapper, UserInterfaceInfo> implements UserInterfaceService {

    @Resource
    private UserService userService;

    @Override
    public void invokeCount(long interfaceId, long userId) {
        if (interfaceId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaUpdateWrapper<UserInterfaceInfo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserInterfaceInfo::getInterfaceId, interfaceId);
        wrapper.eq(UserInterfaceInfo::getUserId, userId);
        wrapper.setSql("total_num=total_num + 1, left_num=left_num - 1");
        this.update(wrapper);
    }

    @Override
    public void addUserInterface(Long interfaceId, HttpServletRequest request) {
        //获取用户ID
        UserVo userVo = userService.getLoginUser(request);
        Long userId = userVo.getId();
        //查询记录是否存在
        LambdaQueryWrapper<UserInterfaceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInterfaceInfo::getUserId, userId);
        wrapper.eq(UserInterfaceInfo::getInterfaceId, interfaceId);
        UserInterfaceInfo userInterfaceInfo = this.getOne(wrapper);
        if (userInterfaceInfo != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, CommonConsts.EXIST_ERROR);
        }
        //插入记录
        userInterfaceInfo = new UserInterfaceInfo();
        userInterfaceInfo.setUserId(userId);
        userInterfaceInfo.setInterfaceId(interfaceId);
        userInterfaceInfo.setTotalNum(0);
        userInterfaceInfo.setLeftNum(10);
        this.save(userInterfaceInfo);
    }

    @Override
    public void updateUserInterface(UserInterfaceUpdateDto userInterfaceUpdateDto) {
        Long id = userInterfaceUpdateDto.getId();
        Integer totalNum = userInterfaceUpdateDto.getTotalNum();
        Integer leftNum = userInterfaceUpdateDto.getLeftNum();
        //校验部分参数
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (totalNum == null || totalNum < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (leftNum == null || leftNum < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //查询记录
        LambdaQueryWrapper<UserInterfaceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInterfaceInfo::getId, id);
        UserInterfaceInfo userInterfaceInfo = this.getOne(wrapper);
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //更新记录
        userInterfaceInfo.setTotalNum(totalNum);
        userInterfaceInfo.setLeftNum(leftNum);
        this.updateById(userInterfaceInfo);
    }

    @Override
    public PageBean<UserInterfaceInfo> pageUserInterfaces(UserInterfacePageDto userInterfacePageDto) {
        Long id = userInterfacePageDto.getId();
        Long userId = userInterfacePageDto.getUserId();
        Long interfaceInfoId = userInterfacePageDto.getInterfaceInfoId();
        Integer current = userInterfacePageDto.getCurrent();
        Integer pageSize = userInterfacePageDto.getPageSize();
        if (current == null || current <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, CommonConsts.PAGE_PARAMS_ERROR);
        }
        if (pageSize == null || pageSize < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, CommonConsts.PAGE_PARAMS_ERROR);
        }
        //开启分页
        Page<UserInterfaceInfo> page = new Page<>(current, pageSize);
        //添加查询条件
        LambdaQueryWrapper<UserInterfaceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(id != null, UserInterfaceInfo::getId, id);
        wrapper.eq(userId != null, UserInterfaceInfo::getUserId, userId);
        wrapper.eq(interfaceInfoId != null, UserInterfaceInfo::getInterfaceId, interfaceInfoId);
        this.page(page, wrapper);
        //返回
        return PageBean.of(page.getTotal(), page.getRecords());
    }
}
