package com.example.interfaceinfo.model;

import lombok.Data;

/**
 * @author by
 */
@Data
public class ImgRes {
    /**
     * 响应码
     */
    private Integer code;
    /**
     * 图片地址
     */
    private String imgurl;
    /**
     * 宽度
     */
    private Integer width;
    /**
     * 高度
     */
    private Integer height;
}
