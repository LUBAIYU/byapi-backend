package com.example;

import com.example.client.ByApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ByapiServerApplicationTests {

    private static final String accessKey = "123";
    private static final String secretKey = "123";

    @Test
    void contextLoads() {
        ByApiClient byApiClient = new ByApiClient(accessKey, secretKey);
        System.out.println(byApiClient.getName("уехЩ"));
    }

}
