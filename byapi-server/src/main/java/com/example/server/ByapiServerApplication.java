package com.example.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author by
 */
@SpringBootApplication(scanBasePackages = {"com.example.common", "com.example.server"})
public class ByapiServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ByapiServerApplication.class, args);
    }

}
