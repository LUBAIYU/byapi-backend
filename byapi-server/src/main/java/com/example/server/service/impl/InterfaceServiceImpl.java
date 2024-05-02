package com.example.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.client.ByApiClient;
import com.example.common.constant.CommonConsts;
import com.example.common.constant.InterfaceConsts;
import com.example.common.enums.ErrorCode;
import com.example.common.exception.BusinessException;
import com.example.common.model.dto.InterfaceAddDto;
import com.example.common.model.dto.InterfaceInvokeDto;
import com.example.common.model.dto.InterfacePageDto;
import com.example.common.model.dto.InterfaceUpdateDto;
import com.example.common.model.entity.InterfaceInfo;
import com.example.common.model.entity.User;
import com.example.common.model.vo.UserVo;
import com.example.common.utils.PageBean;
import com.example.server.mapper.InterfaceMapper;
import com.example.server.service.InterfaceService;
import com.example.server.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author by
 */
@Service
public class InterfaceServiceImpl extends ServiceImpl<InterfaceMapper, InterfaceInfo> implements InterfaceService {

    @Resource
    private UserService userService;

    @Override
    public void addInterface(InterfaceAddDto interfaceAddDto) {
        //判断部分参数是否合法
        String name = interfaceAddDto.getName();
        String url = interfaceAddDto.getUrl();
        String method = interfaceAddDto.getMethod();
        Integer status = interfaceAddDto.getStatus();
        if (StringUtils.isAnyBlank(name, url, method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (status == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //添加接口
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtil.copyProperties(interfaceAddDto, interfaceInfo);
        this.save(interfaceInfo);
    }

    @Override
    public void deleteInterface(Long id) {
        //判断接口是否存在
        InterfaceInfo interfaceInfo = this.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断接口是否处于上线状态
        Integer status = interfaceInfo.getStatus();
        if (status == 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, InterfaceConsts.INTERFACE_ONLINE_ERROR);
        }
        //删除
        this.removeById(id);
    }

    @Override
    public void updateInterface(InterfaceUpdateDto interfaceUpdateDto) {
        //判断部分参数是否符合要求
        Long id = interfaceUpdateDto.getId();
        String name = interfaceUpdateDto.getName();
        String url = interfaceUpdateDto.getUrl();
        String method = interfaceUpdateDto.getMethod();
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isAnyBlank(name, url, method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //更新接口
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtil.copyProperties(interfaceUpdateDto, interfaceInfo);
        this.updateById(interfaceInfo);
    }

    @Override
    public PageBean<InterfaceInfo> listInterfacesByPage(InterfacePageDto interfacePageDto) {
        Long id = interfacePageDto.getId();
        String name = interfacePageDto.getName();
        String url = interfacePageDto.getUrl();
        String method = interfacePageDto.getMethod();
        Integer status = interfacePageDto.getStatus();
        Integer current = interfacePageDto.getCurrent();
        Integer pageSize = interfacePageDto.getPageSize();
        //校验分页参数
        if (current == null || current <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, CommonConsts.PAGE_PARAMS_ERROR);
        }
        if (pageSize == null || pageSize < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, CommonConsts.PAGE_PARAMS_ERROR);
        }
        //添加分页条件
        Page<InterfaceInfo> page = new Page<>(current, pageSize);
        //添加查询条件
        LambdaQueryWrapper<InterfaceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(id != null, InterfaceInfo::getId, id);
        wrapper.like(StrUtil.isNotBlank(name), InterfaceInfo::getName, name);
        wrapper.like(StrUtil.isNotBlank(url), InterfaceInfo::getUrl, url);
        wrapper.eq(StrUtil.isNotBlank(method), InterfaceInfo::getMethod, method);
        wrapper.eq(status != null, InterfaceInfo::getStatus, status);
        //查询
        this.page(page, wrapper);
        //获取记录
        long total = page.getTotal();
        List<InterfaceInfo> records = page.getRecords();
        //返回
        return PageBean.of(total, records);
    }

    @Override
    public void alterStatus(Long id, Integer status) {
        if (id <= 0 || status < 0 || status > 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //查询接口
        InterfaceInfo interfaceInfo = this.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //判断状态是否一致
        if (status.equals(interfaceInfo.getStatus())) {
            return;
        }
        interfaceInfo.setStatus(status);
        this.updateById(interfaceInfo);
    }

    @Override
    public Object invokeInterface(InterfaceInvokeDto interfaceInvokeDto, HttpServletRequest request) {
        Long id = interfaceInvokeDto.getId();
        String userRequestParams = interfaceInvokeDto.getUserRequestParams();
        //判断参数是否为空
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断接口是否存在
        InterfaceInfo interfaceInfo = this.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //获取当前登录用户
        UserVo loginUser = userService.getLoginUser(request);
        User user = userService.getById(loginUser.getId());
        String accessKey = user.getAccessKey();
        String secretKey = user.getSecretKey();
        //如果没有签名和密钥，则抛出异常
        if (StringUtils.isAnyBlank(accessKey, secretKey)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //创建一个SDK客户端
        ByApiClient byApiClient = new ByApiClient(accessKey, secretKey);
        return byApiClient.getName(userRequestParams);
    }
}
