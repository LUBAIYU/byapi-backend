package com.example.client;

import cn.hutool.http.HttpRequest;
import com.example.util.SignUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * sdk客户端
 *
 * @author by
 */
public class ByApiClient {

    /**
     * 签名标识
     */
    private final String accessKey;
    /**
     * 密钥
     */
    private final String secretKey;

    /**
     * 网关地址
     */
    private static final String GATEWAY_HOST = "http://localhost:9010";

    /**
     * 加密钥匙
     */
    public static final String BODY_KEY = "body-key";

    public ByApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    /**
     * 获取输入的名称
     *
     * @param name 名称
     * @return 名称
     */
    public String getName(String name) {
        //添加请求头
        Map<String, String> headerMap = this.getHeaderMap(BODY_KEY);
        //发送请求
        HttpRequest httpRequest = HttpRequest.get(GATEWAY_HOST + "/actual/get/name")
                .form("name", name)
                .addHeaders(headerMap);
        //返回请求数据
        return httpRequest.execute().body();
    }

    /**
     * 随机生成图片
     *
     * @return 图片地址
     */
    public String randomImageUrl() {
        //添加请求头
        Map<String, String> headerMap = this.getHeaderMap(BODY_KEY);
        //发送请求
        HttpRequest httpRequest = HttpRequest.get(GATEWAY_HOST + "/actual/random/imageUrl")
                .addHeaders(headerMap);
        //返回请求数据
        return httpRequest.execute().body();
    }


    public Map<String, String> getHeaderMap(String bodyKey) {
        Map<String, String> map = new HashMap<>(3);
        //签名
        map.put("accessKey", accessKey);
        //密钥
        map.put("secretKey", SignUtil.generateSign(bodyKey, secretKey));
        return map;
    }
}
