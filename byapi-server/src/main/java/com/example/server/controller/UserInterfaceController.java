package com.example.server.controller;

import com.example.common.annotation.LoginCheck;
import com.example.common.annotation.MustAdmin;
import com.example.common.enums.ErrorCode;
import com.example.common.exception.BusinessException;
import com.example.common.model.dto.UserInterfacePageDto;
import com.example.common.model.dto.UserInterfaceUpdateDto;
import com.example.common.model.entity.UserInterfaceInfo;
import com.example.common.model.vo.InvokeCountVo;
import com.example.common.utils.PageBean;
import com.example.common.utils.Result;
import com.example.server.service.UserInterfaceService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author by
 */
@RestController
@RequestMapping("/userInterface")
public class UserInterfaceController {

    @Resource
    private UserInterfaceService userInterfaceService;

    @DeleteMapping("/delete")
    @MustAdmin
    public Result<Void> delUserInterface(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        userInterfaceService.removeById(id);
        return Result.success();
    }

    @PutMapping("/update")
    @MustAdmin
    public Result<Void> updateUserInterface(@RequestBody UserInterfaceUpdateDto userInterfaceUpdateDto) {
        if (userInterfaceUpdateDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        userInterfaceService.updateUserInterface(userInterfaceUpdateDto);
        return Result.success();
    }

    @GetMapping("/page")
    @MustAdmin
    public Result<PageBean<UserInterfaceInfo>> pageUserInterfaces(UserInterfacePageDto userInterfacePageDto) {
        if (userInterfacePageDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        PageBean<UserInterfaceInfo> pageBean = userInterfaceService.pageUserInterfaces(userInterfacePageDto);
        return Result.success(pageBean);
    }

    @PostMapping("/add/count")
    @LoginCheck
    public Result<Void> addInvokeCount(Long interfaceId, HttpServletRequest request) {
        if (interfaceId == null || interfaceId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        userInterfaceService.addInvokeCount(interfaceId, request);
        return Result.success();
    }

    @GetMapping("/invoke/count")
    @LoginCheck
    public Result<List<InvokeCountVo>> getInvokeCountList() {
        List<InvokeCountVo> invokeCountVoList = userInterfaceService.getInvokeCountList();
        return Result.success(invokeCountVoList);
    }
}
