package com.aviation.weather.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 카프카 프로듀서 설정
 */
@Configuration
public class KafkaConfig {
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    @Value("${spring.kafka.producer.acks:all}")
    private String acks;
    
    @Value("${spring.kafka.producer.retries:3}")
    private Integer retries;
    
    @Value("${spring.kafka.producer.batch-size:16384}")
    private Integer batchSize;
    
    @Value("${spring.kafka.producer.linger-ms:1}")
    private Integer lingerMs;
    
    @Value("${spring.kafka.producer.buffer-memory:33554432}")
    private Long bufferMemory;
    
    @Value("${spring.kafka.producer.compression-type:gzip}")
    private String compressionType;
    
    @Value("${spring.kafka.producer.enable-idempotence:true}")
    private Boolean enableIdempotence;
    
    @Value("${spring.kafka.producer.request-timeout-ms:30000}")
    private Integer requestTimeoutMs;
    
    @Value("${spring.kafka.producer.delivery-timeout-ms:120000}")
    private Integer deliveryTimeoutMs;
    
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        
        // 기본 설정
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        
        // 신뢰성 설정
        configProps.put(ProducerConfig.ACKS_CONFIG, acks);
        configProps.put(ProducerConfig.RETRIES_CONFIG, retries);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence);
        
        // 성능 설정
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType);
        
        // 타임아웃 설정
        configProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs);
        configProps.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeoutMs);
        
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
