package com.example.interfaceinfo.controller;

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
}
