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
import com.example.common.model.entity.UserInterfaceInfo;
import com.example.common.model.vo.InterfaceVo;
import com.example.common.model.vo.UserVo;
import com.example.common.utils.PageBean;
import com.example.server.mapper.InterfaceMapper;
import com.example.server.service.InterfaceService;
import com.example.server.service.UserInterfaceService;
import com.example.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author by
 */
@Service
@Slf4j
public class InterfaceServiceImpl extends ServiceImpl<InterfaceMapper, InterfaceInfo> implements InterfaceService {

    @Resource
    private UserService userService;
    @Resource
    private InterfaceService interfaceService;
    @Resource
    private UserInterfaceService userInterfaceService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

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
        //判断接口是否处于关闭状态
        if (interfaceInfo.getStatus() == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, InterfaceConsts.INTERFACE_CLOSE);
        }
        //获取当前登录用户ID
        UserVo loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        //判断用户是否有权限
        User user = userService.getById(userId);
        String accessKey = user.getAccessKey();
        String secretKey = user.getSecretKey();
        //如果没有签名和密钥，则抛出异常
        if (StringUtils.isAnyBlank(accessKey, secretKey)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //判断用户是否还有调用次数
        LambdaQueryWrapper<UserInterfaceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInterfaceInfo::getInterfaceId, id);
        wrapper.eq(UserInterfaceInfo::getUserId, userId);
        UserInterfaceInfo userInterfaceInfo = userInterfaceService.getOne(wrapper);
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        if (userInterfaceInfo.getLeftNum() == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, InterfaceConsts.INVOKE_COUNT_ERROR);
        }
        //创建一个SDK客户端
        ByApiClient byApiClient = new ByApiClient(accessKey, secretKey);
        //利用反射根据接口名称动态调用接口
        Class<? extends ByApiClient> byApiClientClass = byApiClient.getClass();
        //根据接口名称获取方法
        Method method;
        try {
            //判断用户是否传递了参数
            if (StrUtil.isNotBlank(userRequestParams)) {
                method = byApiClientClass.getMethod(interfaceInfo.getName(), String.class);
                //调用方法
                return method.invoke(byApiClient, userRequestParams);
            }
            method = byApiClientClass.getMethod(interfaceInfo.getName());
            //调用方法
            return method.invoke(byApiClient);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, InterfaceConsts.NOT_EXIST_ERROR);
        }
    }

    @Override
    public void openPermission(Long interfaceId, HttpServletRequest request) {
        //获取用户ID
        UserVo userVo = userService.getLoginUser(request);
        Long userId = userVo.getId();
        //为当前用户分配密钥
        userService.applyKey(request);
        //添加用户接口关系记录
        userInterfaceService.addUserInterface(interfaceId, userId);
    }

    @Override
    public InterfaceVo getInterfaceById(Long id, HttpServletRequest request) {
        //获取当前登录用户ID
        UserVo userVo = userService.getLoginUser(request);
        Long userId = userVo.getId();
        //判断缓存是否存在
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String key = String.format(CommonConsts.GET_INTERFACE_BY_ID_KEY, userId, id);
        InterfaceVo interfaceVo = (InterfaceVo) valueOperations.get(key);
        if (interfaceVo != null) {
            return interfaceVo;
        }
        //判断接口是否存在
        LambdaQueryWrapper<InterfaceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InterfaceInfo::getId, id);
        InterfaceInfo interfaceInfo = this.getOne(wrapper);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //根据用户ID和接口ID查询当前用户调用次数
        LambdaQueryWrapper<UserInterfaceInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInterfaceInfo::getUserId, userId);
        queryWrapper.eq(UserInterfaceInfo::getInterfaceId, id);
        UserInterfaceInfo userInterfaceInfo = userInterfaceService.getOne(queryWrapper);
        //封装数据
        interfaceVo = new InterfaceVo();
        BeanUtil.copyProperties(interfaceInfo, interfaceVo);
        if (userInterfaceInfo != null) {
            interfaceVo.setLeftNum(userInterfaceInfo.getLeftNum());
            interfaceVo.setTotalNum(userInterfaceInfo.getTotalNum());
        }
        //设置缓存
        try {
            //设置缓存时间30分钟
            valueOperations.set(key, interfaceVo, 30, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("redis set key error", e);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, CommonConsts.CACHE_SET_ERROR);
        }
        return interfaceVo;
    }

    @Override
    public List<InterfaceVo> listInvokeRecords(HttpServletRequest request) {
        //获取用户ID
        UserVo userVo = userService.getLoginUser(request);
        Long userId = userVo.getId();
        //判断缓存是否为空
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        String key = String.format(CommonConsts.LIST_INVOKE_RECORDS_KEY, userId);
        List<InterfaceVo> interfaceVoList = (List<InterfaceVo>) valueOperations.get(key);
        if (!CollectionUtils.isEmpty(interfaceVoList)) {
            return interfaceVoList;
        }
        //根据用户ID去查询用户接口关联表
        LambdaQueryWrapper<UserInterfaceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInterfaceInfo::getUserId, userId);
        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceService.list(wrapper);
        interfaceVoList = new ArrayList<>();
        //如果为空直接返回
        if (CollectionUtils.isEmpty(userInterfaceInfoList)) {
            return interfaceVoList;
        }
        //封装数据
        interfaceVoList = userInterfaceInfoList.stream().map(userInterfaceInfo -> {
            InterfaceInfo interfaceInfo = interfaceService.getById(userInterfaceInfo.getInterfaceId());
            InterfaceVo interfaceVo = new InterfaceVo();
            BeanUtil.copyProperties(interfaceInfo, interfaceVo);
            interfaceVo.setTotalNum(userInterfaceInfo.getTotalNum());
            interfaceVo.setLeftNum(userInterfaceInfo.getLeftNum());
            return interfaceVo;
        }).collect(Collectors.toList());
        //设置缓存
        try {
            valueOperations.set(key, interfaceVoList, 30, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("redis set key error", e);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, CommonConsts.CACHE_SET_ERROR);
        }
        return interfaceVoList;
    }

    @Override
    public String getCodeExample(Long interfaceId) {
        InterfaceInfo interfaceInfo = this.getById(interfaceId);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return interfaceInfo.getCodeExample();
    }
}
