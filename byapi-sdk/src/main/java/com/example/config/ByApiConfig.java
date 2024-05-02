package com.example.config;

import com.example.client.ByApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * sdk配置类
 *
 * @author by
 */
@Configuration
@ConfigurationProperties(prefix = "byapi.client")
@Data
@ComponentScan
public class ByApiConfig {

    private String accessKey;
    private String secretKey;

    @Bean
    public ByApiClient byApiClient() {
        return new ByApiClient(accessKey, secretKey);
    }
}
