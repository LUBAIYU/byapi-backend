package com.example;

import com.example.client.ByApiClient;
import com.example.server.ByapiServerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest(classes = {ByapiServerApplication.class})
class ByapiServerApplicationTests {

    @Resource
    private ByApiClient byApiClient;

    @Test
    void contextLoads() {
        System.out.println(byApiClient.getName("张三"));
    }

}
