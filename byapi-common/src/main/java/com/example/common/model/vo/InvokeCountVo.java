package com.example.common.model.vo;

import lombok.Data;

/**
 * 接口调用次数返回类
 *
 * @author by
 */
@Data
public class InvokeCountVo {
    /**
     * 接口名称
     */
    private String name;
    /**
     * 调用次数
     */
    private Integer count;
}
