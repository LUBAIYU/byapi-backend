package com.example.common.aop;

import com.example.common.constant.UserConsts;
import com.example.common.enums.ErrorCode;
import com.example.common.exception.BusinessException;
import com.example.common.model.vo.UserVo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录校验切面类
 *
 * @author by
 */
@Component
@Aspect
public class LoginAspect {
    @Around("@annotation(com.example.common.annotation.LoginCheck)")
    public Object loginCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取请求对象
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        //获取登录用户
        Object object = request.getSession().getAttribute(UserConsts.USER_LOGIN_STATE);
        UserVo userVo = (UserVo) object;
        //判断用户是否登录
        if (userVo == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        //放行
        return joinPoint.proceed();
    }
}
