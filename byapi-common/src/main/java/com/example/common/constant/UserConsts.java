package com.example.common.constant;

/**
 * 用户常量类
 *
 * @author by
 */
public interface UserConsts {
    /**
     * 用户登录态
     */
    String USER_LOGIN_STATE = "user_login_state";
    Integer USER_NAME_LENGTH = 4;
    Integer USER_PASSWORD_LENGTH = 8;
    String USER_NAME_ERROR = "用户名长度不能小于4位！";
    String USER_PASSWORD_ERROR = "密码长度不能小于8位！";
    String USER_PARAMS_ERROR = "用户名或密码错误！";
    String PASSWORD_NOT_EQUAL = "密码不一致！";
    String USER_NAME_EXIST = "用户名已经存在！";
    String CODE = "code";
    String EMAIL = "email";
    String VER_CODE = "verCode";
    String DELAY_TASK_ERROR = "延迟任务失败！";
    String SEND_MAIL_ERROR = "发送邮件失败！";
    String EMAIL_PARAMS_ERROR = "邮箱或验证码错误！";
    String EMAIL_EXIST = "邮箱已经存在！";
}
