package com.example.client;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
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
        //发送请求
        HttpRequest httpRequest = HttpRequest.get("http://localhost:9020/actual/get/name").form("name",name);
        //返回请求数据
        return httpRequest.execute().body();
    }

    public Map<String, String> getHeaderMap(String body) {
        Map<String, String> map = new HashMap<>(3);
        //签名
        map.put("accessKey", accessKey);
        //请求参数
        map.put("body", body);
        //密钥
        map.put("secretKey", SignUtil.generateSign(body, secretKey));
        return map;
    }
}
