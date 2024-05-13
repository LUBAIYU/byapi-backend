package com.example.server.controller;

import com.example.common.annotation.LoginCheck;
import com.example.common.annotation.MustAdmin;
import com.example.common.enums.ErrorCode;
import com.example.common.exception.BusinessException;
import com.example.common.model.dto.InterfaceAddDto;
import com.example.common.model.dto.InterfaceInvokeDto;
import com.example.common.model.dto.InterfacePageDto;
import com.example.common.model.dto.InterfaceUpdateDto;
import com.example.common.model.entity.InterfaceInfo;
import com.example.common.model.vo.InterfaceVo;
import com.example.common.utils.PageBean;
import com.example.common.utils.Result;
import com.example.server.service.InterfaceService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/interface")
public class InterfaceController {

    @Resource
    private InterfaceService interfaceService;

    @PostMapping("/add")
    @MustAdmin
    public Result<Void> addInterface(@RequestBody InterfaceAddDto interfaceAddDto) {
        if (interfaceAddDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        interfaceService.addInterface(interfaceAddDto);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    @MustAdmin
    public Result<Void> deleteInterface(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        interfaceService.deleteInterface(id);
        return Result.success();
    }

    @PutMapping("/update")
    @MustAdmin
    public Result<Void> updateInterface(@RequestBody InterfaceUpdateDto interfaceUpdateDto) {
        if (interfaceUpdateDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        interfaceService.updateInterface(interfaceUpdateDto);
        return Result.success();
    }

    @GetMapping("/page")
    @LoginCheck
    public Result<PageBean<InterfaceInfo>> listInterfacesByPage(InterfacePageDto interfacePageDto) {
        if (interfacePageDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        PageBean<InterfaceInfo> pageBean = interfaceService.listInterfacesByPage(interfacePageDto);
        return Result.success(pageBean);
    }

    @PutMapping("/alter/status")
    @MustAdmin
    public Result<Void> alterStatus(Long id, Integer status) {
        if (id == null || status == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        interfaceService.alterStatus(id, status);
        return Result.success();
    }

    @GetMapping("/get/{id}")
    @LoginCheck
    public Result<InterfaceVo> getInterfaceById(@PathVariable Long id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceVo interfaceVo = interfaceService.getInterfaceById(id, request);
        return Result.success(interfaceVo);
    }

    @PostMapping("/invoke")
    @LoginCheck
    public Result<Object> invokeInterface(@RequestBody InterfaceInvokeDto interfaceInvokeDto, HttpServletRequest request) {
        if (interfaceInvokeDto == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Object res = interfaceService.invokeInterface(interfaceInvokeDto, request);
        return Result.success(res);
    }

    @PostMapping("/open")
    @LoginCheck
    public Result<Void> openPermission(Long interfaceId, HttpServletRequest request) {
        if (interfaceId == null || interfaceId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        interfaceService.openPermission(interfaceId, request);
        return Result.success();
    }

    @GetMapping("/list/record")
    @LoginCheck
    public Result<List<InterfaceVo>> listInvokeRecords(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<InterfaceVo> interfaceVos = interfaceService.listInvokeRecords(request);
        return Result.success(interfaceVos);
    }

    @GetMapping("/get/code")
    @LoginCheck
    public Result<String> getCodeExample(Long interfaceId) {
        if (interfaceId == null || interfaceId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String codeExample = interfaceService.getCodeExample(interfaceId);
        return Result.success(codeExample);
    }
}
