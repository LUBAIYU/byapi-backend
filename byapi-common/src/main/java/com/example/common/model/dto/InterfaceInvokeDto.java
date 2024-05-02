package com.example.common.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 接口调用请求体
 *
 * @author by
 */
@Data
public class InterfaceInvokeDto implements Serializable {

    /**
     * 接口ID
     */
    private Long id;
    /**
     * 请求参数，不必需
     */
    private String userRequestParams;
}
