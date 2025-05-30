package com.aviation.weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 항공 기상 데이터 카프카 프로듀서 메인 애플리케이션
 * 
 * METAR, AIRMET, SIGMET 데이터를 수집하여 각각의 카프카 토픽으로 전송
 * XML 데이터를 JSON 형태로 변환하여 처리
 * 
 * @author Aviation Weather Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableScheduling
public class AviationWeatherProducerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AviationWeatherProducerApplication.class, args);
    }
}
