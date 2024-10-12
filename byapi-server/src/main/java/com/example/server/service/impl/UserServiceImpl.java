package com.example.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.constant.CommonConsts;
import com.example.common.constant.UserConsts;
import com.example.common.enums.ErrorCode;
import com.example.common.enums.RoleEnum;
import com.example.common.exception.BusinessException;
import com.example.common.model.dto.*;
import com.example.common.model.entity.User;
import com.example.common.model.vo.KeyVo;
import com.example.common.model.vo.UserVo;
import com.example.common.utils.PageBean;
import com.example.server.mapper.UserMapper;
import com.example.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    /**
     * 邮件发送类
     */
    @Resource
    private JavaMailSenderImpl javaMailSender;

    @Resource
    private UserMapper userMapper;

    /**
     * 创建线程池
     */
    private final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(10, new ThreadPoolExecutor.AbortPolicy());

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
        User user = this.lambdaQuery()
                .eq(User::getUserAccount, userAccount)
                .one();
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, UserConsts.USER_PARAMS_ERROR);
        }

        //判断用户是否被禁用
        if (user.getStatus() == 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, UserConsts.ACCOUNT_FORBIDDEN);
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
        User user = this.lambdaQuery()
                .eq(User::getUserAccount, userAccount)
                .one();
        if (user != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, UserConsts.USER_NAME_EXIST);
        }

        //生成一个随机的盐
        String salt = RandomUtil.randomString(4);
        //对密码进行加密
        String encryptPassword = DigestUtil.md5Hex(userPassword + salt);
        //插入用户数据
        user = new User();
        user.setUserAccount(userAccount);
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
    public void updateUser(UserUpdateDto userUpdateDto, HttpServletRequest request) {
        Long id = userUpdateDto.getId();
        String userAccount = userUpdateDto.getUserAccount();
        String email = userUpdateDto.getEmail();
        Integer status = userUpdateDto.getStatus();
        //判断部分参数是否合法
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StrUtil.isBlank(userAccount) && StrUtil.isBlank(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //查询邮箱是否存在
        User user = this.lambdaQuery()
                .eq(User::getEmail, email)
                .one();

        //获取当前登录用户
        UserVo userVo = this.getLoginUser(request);
        //如果邮箱存在且邮箱为其他用户所有，则抛出异常
        if (user != null && !userVo.getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, UserConsts.EMAIL_EXIST);
        }
        if (status == null) {
            userUpdateDto.setStatus(0);
        }
        //更新
        user = new User();
        BeanUtil.copyProperties(userUpdateDto, user);
        this.updateById(user);
    }

    @Override
    public PageBean<User> listUsersByPage(UserPageDto userPageDto) {
        // 构建分页条件
        IPage<User> pageCondition = new Page<>(userPageDto.getCurrent(), userPageDto.getPageSize());
        // 查询
        IPage<User> page = userMapper.listUsersByPage(pageCondition, userPageDto);
        // 返回
        return PageBean.of(page.getTotal(), page.getRecords());
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

    @Override
    public KeyVo applyKey(HttpServletRequest request) {
        //获取登录用户ID
        UserVo userVo = this.getLoginUser(request);
        Long userId = userVo.getId();
        //判断用户是否存在
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //给用户生成随机密钥
        String accessKey = RandomUtil.randomString(32);
        String secretKey = RandomUtil.randomString(32);
        //保存密钥
        user.setAccessKey(accessKey);
        user.setSecretKey(secretKey);
        this.updateById(user);
        //返回生成的密钥
        KeyVo keyVo = new KeyVo();
        keyVo.setAccessKey(accessKey);
        keyVo.setSecretKey(secretKey);
        return keyVo;
    }

    @Override
    public void sendEmail(String email, HttpServletRequest request) {
        HttpSession session = request.getSession();
        //随机生成验证码
        String verCode = RandomUtil.randomNumbers(6);
        //发送时间
        String time = DateUtil.formatDateTime(new Date());
        //保存验证码的map
        Map<String, String> map = new HashMap<>(4);
        map.put(UserConsts.CODE, verCode);
        map.put(UserConsts.EMAIL, email);
        //验证码和邮箱一起放入session
        session.setAttribute(UserConsts.VER_CODE, map);
        Object object = session.getAttribute(UserConsts.VER_CODE);
        @SuppressWarnings("unchecked")
        Map<String, String> codeMap = (Map<String, String>) object;
        //创建计时线程
        try {
            //5分钟后移除验证码
            scheduledExecutorService.schedule(() -> {
                if (email.equals(codeMap.get(UserConsts.EMAIL))) {
                    session.removeAttribute(UserConsts.VER_CODE);
                }
            }, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, UserConsts.DELAY_TASK_ERROR);
        }
        //发送邮件
        MimeMessage mimeMessage;
        MimeMessageHelper helper;
        try {
            // 解决本地DNS未配置 ip->域名场景下，邮件发送太慢的问题
            System.getProperties().setProperty("mail.mime.address.usecanonicalhostname", "false");
            //发送复杂的邮件
            mimeMessage = javaMailSender.createMimeMessage();
            Session messageSession = mimeMessage.getSession();
            //解决本地DNS未配置 ip->域名场景下，邮件发送太慢的问题
            messageSession.getProperties().setProperty("mail.smtp.localhost", "myComputer");
            //组装
            helper = new MimeMessageHelper(mimeMessage, true);
            //邮件标题
            helper.setSubject("【By API】 验证码");
            //ture为支持识别html标签
            helper.setText("<h3>\n" +
                    "\t<span style=\"font-size:16px;\">亲爱的用户：</span> \n" +
                    "</h3>\n" +
                    "<p>\n" +
                    "\t<span style=\"font-size:14px;\">&nbsp;&nbsp;&nbsp;&nbsp;</span><span style=\"font-size:14px;\">&nbsp; <span style=\"font-size:16px;\">&nbsp;&nbsp;您好！您正在进行邮箱验证，本次请求的验证码为：<span style=\"font-size:24px;color:#FFE500;\"> " + verCode + "</span>，本验证码5分钟内有效，请勿泄露和转发。如非本人操作，请忽略该邮件。</span></span>\n" +
                    "</p>\n" +
                    "<p style=\"text-align:right;\">\n" +
                    "\t<span style=\"background-color:#FFFFFF;font-size:16px;color:#000000;\"><span style=\"color:#000000;font-size:16px;background-color:#FFFFFF;\"><span class=\"token string\" style=\"font-family:&quot;font-size:16px;color:#000000;line-height:normal !important;background-color:#FFFFFF;\">By API</span></span></span> \n" +
                    "</p>\n" +
                    "<p style=\"text-align:right;\">\n" +
                    "\t<span style=\"background-color:#FFFFFF;font-size:14px;\"><span style=\"color:#FF9900;font-size:18px;\"><span class=\"token string\" style=\"font-family:&quot;font-size:16px;color:#000000;line-height:normal !important;\"><span style=\"font-size:16px;color:#000000;background-color:#FFFFFF;\">" + time + "</span><span style=\"font-size:18px;color:#000000;background-color:#FFFFFF;\"></span></span></span></span> \n" +
                    "</p>", true);
            //收件人
            helper.setTo(email);
            //发送方
            helper.setFrom("1296800094@qq.com");
            //异步发送邮件
            scheduledExecutorService.execute(() -> javaMailSender.send(mimeMessage));
        } catch (Exception e) {
            //邮箱是无效的，或者发送失败
            throw new BusinessException(ErrorCode.PARAMS_ERROR, UserConsts.SEND_MAIL_ERROR);
        }
    }

    @Override
    public UserVo emailLogin(EmailDto emailDto, HttpServletRequest request) {
        String email = emailDto.getEmail();
        String verCode = emailDto.getVerCode();
        if (StringUtils.isAnyBlank(email, verCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断邮箱是否存在
        User user = this.lambdaQuery()
                .eq(User::getEmail, email)
                .one();
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, UserConsts.EMAIL_PARAMS_ERROR);
        }
        //判断用户是否被禁用
        if (user.getStatus() == 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, UserConsts.ACCOUNT_FORBIDDEN);
        }
        //获取session中的验证码
        Object object = request.getSession().getAttribute(UserConsts.VER_CODE);
        @SuppressWarnings("unchecked")
        Map<String, String> map = (Map<String, String>) object;
        String sEmail = map.get(UserConsts.EMAIL);
        String code = map.get(UserConsts.CODE);
        //校验邮箱和验证码
        if (!email.equals(sEmail) || !verCode.equals(code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, UserConsts.EMAIL_PARAMS_ERROR);
        }
        //用户信息脱敏
        UserVo userVo = new UserVo();
        BeanUtil.copyProperties(user, userVo);
        //保存用户登录态
        request.getSession().setAttribute(UserConsts.USER_LOGIN_STATE, userVo);
        return userVo;
    }

    @Override
    public void emailRegister(EmailDto emailDto) {
        String email = emailDto.getEmail();
        String verCode = emailDto.getVerCode();
        if (StringUtils.isAnyBlank(email, verCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断邮箱是否存在
        User user = this.lambdaQuery()
                .eq(User::getEmail, email)
                .one();
        if (user != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, UserConsts.EMAIL_PARAMS_ERROR);
        }
        //插入数据
        user = new User();
        user.setEmail(email);
        user.setStatus(0);
        user.setUserRole(RoleEnum.USER.getRole());
        user.setUserName(email);
        this.save(user);
    }

    @Override
    public KeyVo getKeyById(HttpServletRequest request) {
        UserVo userVo = this.getLoginUser(request);
        Long userId = userVo.getId();
        User user = this.lambdaQuery()
                .eq(User::getId, userId)
                .one();
        KeyVo keyVo = new KeyVo();
        keyVo.setAccessKey(user.getAccessKey());
        keyVo.setSecretKey(user.getSecretKey());
        return keyVo;
    }

    @Override
    public void downloadJar(HttpServletResponse response) {
        try {
            //设置响应类型
            response.setContentType("application/java-archive");
            //设置响应头，指定下载的文件名
            response.setHeader("Content-Disposition", "attachment; filename=byapi-sdk.jar");
            //指定jar包路径
            String filePath = "D:/idea/project/byapi-backend/byapi-sdk/target/byapi-sdk-0.0.1-SNAPSHOT.jar";
            File jarFile = new File(filePath);
            try (InputStream inputStream = new FileInputStream(jarFile);
                 OutputStream outputStream = response.getOutputStream()) {
                //将jar包写入响应体中
                IoUtil.copy(inputStream, outputStream);
            } catch (IOException e) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, CommonConsts.SDK_DOWNLOAD_ERROR);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, CommonConsts.SDK_DOWNLOAD_ERROR);
        }
    }
}




