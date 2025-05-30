package com.aviation.weather.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .codecs(configurer -> {
                // 버퍼 크기를 10MB로 설정 (기본값: 256KB = 262144)
                configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024); // 10MB
            })
            .build();
    }
}
