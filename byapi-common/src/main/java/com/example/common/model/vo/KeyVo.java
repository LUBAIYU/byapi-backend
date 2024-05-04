package com.example.common.model.vo;

import lombok.Data;

/**
 * 密钥视图对象
 *
 * @author by
 */
@Data
public class KeyVo {
    /**
     * 访问密钥
     */
    private String accessKey;
    /**
     * 私钥
     */
    private String secretKey;
}
