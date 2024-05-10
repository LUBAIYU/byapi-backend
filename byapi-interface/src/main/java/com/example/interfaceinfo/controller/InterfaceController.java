package com.example.interfaceinfo.controller;

import cn.hutool.http.HttpRequest;
import com.example.interfaceinfo.model.ImgRes;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实际提供数据的接口
 *
 * @author by
 */
@RestController
@RequestMapping("/actual")
public class InterfaceController {

    @GetMapping("/get/name")
    public String getName(String name) {
        return "你好，" + name;
    }

    @GetMapping("/random/imageUrl")
    public String randomImageUrl() {
        //获取随机图片
        HttpRequest request = HttpRequest.get("https://api.btstu.cn/sjbz/api.php?format=json");
        String json = request.execute().body();
        //解析JSON
        Gson gson = new Gson();
        ImgRes imgRes = gson.fromJson(json, ImgRes.class);
        return imgRes.getImgurl();
    }
}
