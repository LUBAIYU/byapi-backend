package com.example.common.model.dto;

import com.example.common.utils.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分页条件请求体
 *
 * @author by
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfacePageDto extends PageRequest {
    /**
     * 主键
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 接口状态（0-关闭，1-开启）
     */
    private Integer status;
}
