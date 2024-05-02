package com.example.common.utils;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用分页参数
 *
 * @author by
 */
@Data
public class PageRequest implements Serializable {
    /**
     * 当前页码
     */
    private Integer current;
    /**
     * 每页记录数
     */
    private Integer pageSize;
}
