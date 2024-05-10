package com.example.interfaceinfo.model;

import lombok.Data;

/**
 * @author by
 */
@Data
public class TalkRes {
    /**
     * 状态码
     */
    private String code;
    /**
     * 文本内容
     */
    private String content;
    /**
     * 错误提示信息
     */
    private String msg;
}
