package com.example.common.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页工具类
 *
 * @param <T>
 * @author by
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class PageBean<T> {
    /**
     * 总条数
     */
    private Long total;
    /**
     * 总记录数
     */
    private List<T> records;
}
