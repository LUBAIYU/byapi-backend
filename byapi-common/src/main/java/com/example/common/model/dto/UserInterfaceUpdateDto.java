package com.example.common.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户接口关系更新请求
 *
 * @author by
 */
@Data
public class UserInterfaceUpdateDto implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;
}
