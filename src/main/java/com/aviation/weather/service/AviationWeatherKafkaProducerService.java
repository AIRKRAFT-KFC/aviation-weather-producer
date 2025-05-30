package com.aviation.weather.service;

import com.aviation.weather.config.AviationWeatherProperties;
import com.aviation.weather.model.MetarData;
import com.aviation.weather.model.PirepData;
import com.aviation.weather.model.HazardWeatherData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 항공 기상 데이터를 카프카로 전송하는 프로듀서 서비스
 * METAR와 AIRMET/SIGMET 데이터를 각각 다른 토픽으로 분리하여 전송
 */
@Service
public class AviationWeatherKafkaProducerService {
    
    private static final Logger logger = LoggerFactory.getLogger(AviationWeatherKafkaProducerService.class);
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final AviationWeatherProperties properties;
    
    @Autowired
    public AviationWeatherKafkaProducerService(KafkaTemplate<String, String> kafkaTemplate,
                                               ObjectMapper objectMapper,
                                               AviationWeatherProperties properties) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }
    
    /**
     * METAR 데이터를 카프카 토픽으로 전송
     * 
     * @param metarDataList METAR 데이터 리스트
     */
    public void sendMetarData(List<MetarData> metarDataList) {
        if (metarDataList == null || metarDataList.isEmpty()) {
            logger.info("전송할 METAR 데이터가 없습니다.");
            return;
        }
        
        logger.info("METAR 데이터 카프카 전송 시작: {} 건", metarDataList.size());
        String topicName = properties.getTopics().getMetar();
        
        int successCount = 0;
        int failureCount = 0;
        
        for (MetarData metarData : metarDataList) {
            try {
                // JSON 직렬화
                String jsonData = objectMapper.writeValueAsString(metarData);
                
                // 키 생성: 공항코드_METAR_타임스탬프
                String key = generateMetarKey(metarData);
                
                // 카프카로 전송
                CompletableFuture<SendResult<String, String>> future = 
                    kafkaTemplate.send(topicName, key, jsonData);
                
                // 비동기 콜백 설정
                future.whenComplete((result, ex) -> {
                    if (ex == null) {
                        logger.debug("METAR 데이터 전송 성공 - Key: {}, Partition: {}, Offset: {}", 
                                key, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                    } else {
                        logger.error("METAR 데이터 전송 실패 - Key: {}, Error: {}", key, ex.getMessage());
                    }
                });
                
                successCount++;
                
            } catch (Exception e) {
                logger.error("METAR 데이터 직렬화 실패: {}", 
                    metarData.getIcaoCode() != null ? metarData.getIcaoCode() : "UNKNOWN", e);
                failureCount++;
            }
        }
        
        logger.info("METAR 데이터 카프카 전송 완료 - 성공: {}, 실패: {}", successCount, failureCount);
    }
    
    /**
     * PIREP 데이터를 카프카 토픽으로 전송
     * 
     * @param pirepDataList PIREP 데이터 리스트
     */
    public void sendPirepData(List<PirepData> pirepDataList) {
        if (pirepDataList == null || pirepDataList.isEmpty()) {
            logger.info("전송할 PIREP 데이터가 없습니다.");
            return;
        }
        
        logger.info("PIREP 데이터 카프카 전송 시작: {} 건", pirepDataList.size());
        String topicName = properties.getTopics().getPirep();
        
        int successCount = 0;
        int failureCount = 0;
        
        for (PirepData pirepData : pirepDataList) {
            try {
                // JSON 직렬화
                String jsonData = objectMapper.writeValueAsString(pirepData);
                
                // 키 생성: PIREP_시간_위치
                String key = generatePirepKey(pirepData);
                
                // 카프카로 전송
                CompletableFuture<SendResult<String, String>> future = 
                    kafkaTemplate.send(topicName, key, jsonData);
                
                // 비동기 콜백 설정
                future.whenComplete((result, ex) -> {
                    if (ex == null) {
                        logger.debug("PIREP 데이터 전송 성공 - Key: {}, Partition: {}, Offset: {}", 
                                key, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                    } else {
                        logger.error("PIREP 데이터 전송 실패 - Key: {}, Error: {}", key, ex.getMessage());
                    }
                });
                
                successCount++;
                
            } catch (Exception e) {
                logger.error("PIREP 데이터 직렬화 실패: {}", 
                    pirepData.getRawObservation() != null ? pirepData.getRawObservation().substring(0, Math.min(50, pirepData.getRawObservation().length())) : "UNKNOWN", e);
                failureCount++;
            }
        }
        
        logger.info("PIREP 데이터 카프카 전송 완료 - 성공: {}, 실패: {}", successCount, failureCount);
    }
    
    /**
     * SIGMET 데이터를 카프카 토픽으로 전송
     * 
     * @param sigmetDataList SIGMET 데이터 리스트
     */
    public void sendSigmetData(List<HazardWeatherData> sigmetDataList) {
        if (sigmetDataList == null || sigmetDataList.isEmpty()) {
            logger.info("전송할 SIGMET 데이터가 없습니다.");
            return;
        }
        
        logger.info("SIGMET 데이터 카프카 전송 시작: {} 건", sigmetDataList.size());
        String topicName = properties.getTopics().getSigmet();
        
        sendHazardWeatherData(sigmetDataList, topicName, "SIGMET");
    }
    
    /**
     * 위험 기상 데이터 전송 공통 메서드
     * 
     * @param hazardDataList 위험 기상 데이터 리스트
     * @param topicName 토픽명
     * @param dataType 데이터 타입 (AIRMET/SIGMET)
     */
    private void sendHazardWeatherData(List<HazardWeatherData> hazardDataList, String topicName, String dataType) {
        int successCount = 0;
        int failureCount = 0;
        
        for (HazardWeatherData hazardData : hazardDataList) {
            try {
                // JSON 직렬화
                String jsonData = objectMapper.writeValueAsString(hazardData);
                
                // 키 생성: 데이터타입_경보ID_위험유형
                String key = generateHazardWeatherKey(hazardData, dataType);
                
                // 카프카로 전송
                CompletableFuture<SendResult<String, String>> future = 
                    kafkaTemplate.send(topicName, key, jsonData);
                
                // 비동기 콜백 설정
                future.whenComplete((result, ex) -> {
                    if (ex == null) {
                        logger.debug("{} 데이터 전송 성공 - Key: {}, Partition: {}, Offset: {}", 
                                dataType, key, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                    } else {
                        logger.error("{} 데이터 전송 실패 - Key: {}, Error: {}", dataType, key, ex.getMessage());
                    }
                });
                
                successCount++;
                
            } catch (Exception e) {
                logger.error("{} 데이터 직렬화 실패: {}", dataType, 
                    hazardData.getAirSigmetId() != null ? hazardData.getAirSigmetId() : "UNKNOWN", e);
                failureCount++;
            }
        }
        
        logger.info("{} 데이터 카프카 전송 완료 - 성공: {}, 실패: {}", dataType, successCount, failureCount);
    }
    
    /**
     * METAR 데이터용 키 생성
     */
    private String generateMetarKey(MetarData metarData) {
        StringBuilder keyBuilder = new StringBuilder();
        
        // 새로운 모델 구조에 맞춴 ICAO 코드 추출
        if (metarData.getIcaoCode() != null && !metarData.getIcaoCode().isEmpty()) {
            keyBuilder.append(metarData.getIcaoCode());
        } else {
            keyBuilder.append("UNKNOWN");
        }
        
        keyBuilder.append("_METAR");
        
        if (metarData.getObservationTime() != null) {
            keyBuilder.append("_").append(metarData.getObservationTime().toLocalDate());
        }
        
        return keyBuilder.toString();
    }
    
    /**
     * PIREP 데이터용 키 생성
     */
    private String generatePirepKey(PirepData pirepData) {
        StringBuilder keyBuilder = new StringBuilder();
        
        keyBuilder.append("PIREP");
        
        // PIREP ID 사용 (고유값)
        if (pirepData.getPirepId() != null) {
            keyBuilder.append("_").append(pirepData.getPirepId());
        }
        
        // ICAO ID
        if (pirepData.getIcaoId() != null && !pirepData.getIcaoId().isEmpty()) {
            keyBuilder.append("_").append(pirepData.getIcaoId());
        }
        
        // 위치 정보 (좌표)
        if (pirepData.getLatitude() != null && pirepData.getLongitude() != null) {
            keyBuilder.append("_").append(Math.round(pirepData.getLatitude()))
                      .append("_").append(Math.round(pirepData.getLongitude()));
        }
        
        return keyBuilder.toString();
    }
    
    /**
     * 위험 기상 데이터용 키 생성
     * 
     * @param hazardData 위험 기상 데이터
     * @param dataType 데이터 타입
     * @return 카프카 메시지 키
     */
    private String generateHazardWeatherKey(HazardWeatherData hazardData, String dataType) {
        StringBuilder keyBuilder = new StringBuilder();
        
        keyBuilder.append(dataType);
        
        // airSigmetId 사용
        if (hazardData.getAirSigmetId() != null && !hazardData.getAirSigmetId().isEmpty()) {
            keyBuilder.append("_").append(hazardData.getAirSigmetId());
        }
        
        // 위험 유형
        if (hazardData.getHazard() != null && !hazardData.getHazard().isEmpty()) {
            keyBuilder.append("_").append(hazardData.getHazard().toUpperCase());
        }
        
        // 생성 날짜 (파티셔닝에 도움)
        if (hazardData.getCreationTime() != null) {
            keyBuilder.append("_").append(hazardData.getCreationTime().toLocalDate());
        } else if (hazardData.getValidTimeFrom() != null) {
            // Unix timestamp를 LocalDate로 변환
            try {
                java.time.LocalDate date = java.time.Instant.ofEpochSecond(hazardData.getValidTimeFrom())
                    .atZone(java.time.ZoneOffset.UTC)
                    .toLocalDate();
                keyBuilder.append("_").append(date);
            } catch (Exception e) {
                // 변환 실패 시 기본값 사용
                keyBuilder.append("_").append(java.time.LocalDate.now());
            }
        }
        
        return keyBuilder.toString();
    }
}
