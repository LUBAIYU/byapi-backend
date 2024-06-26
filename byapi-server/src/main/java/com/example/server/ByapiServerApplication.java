package com.example.server;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author by
 */
@SpringBootApplication(scanBasePackages = {"com.example.common", "com.example.server"})
@EnableDubbo
@EnableScheduling
public class ByapiServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ByapiServerApplication.class, args);
    }

}
