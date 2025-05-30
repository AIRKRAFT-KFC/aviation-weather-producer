package com.aviation.weather.service;

import com.aviation.weather.config.AviationWeatherProperties;
import com.aviation.weather.model.MetarData;
import com.aviation.weather.model.PirepData;
import com.aviation.weather.model.HazardWeatherData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 항공 기상 데이터 수집 및 전송 스케줄러
 * 설정된 cron 표현식에 따라 정기적으로 데이터를 수집하고 카프카로 전송
 */
@Service
public class AviationWeatherSchedulerService {
    
    private static final Logger logger = LoggerFactory.getLogger(AviationWeatherSchedulerService.class);
    
    private final AviationWeatherService aviationWeatherService;
    private final AviationWeatherKafkaProducerService kafkaProducerService;
    private final AviationWeatherProperties properties;
    
    // 실행 통계
    private long metarExecutionCount = 0;
    private long pirepExecutionCount = 0;
    private long sigmetExecutionCount = 0;
    private long lastMetarExecutionTime = 0;
    private long lastPirepExecutionTime = 0;
    private long lastSigmetExecutionTime = 0;
    
    @Autowired
    public AviationWeatherSchedulerService(AviationWeatherService aviationWeatherService,
                                           AviationWeatherKafkaProducerService kafkaProducerService,
                                           AviationWeatherProperties properties) {
        this.aviationWeatherService = aviationWeatherService;
        this.kafkaProducerService = kafkaProducerService;
        this.properties = properties;
    }
    
    /**
     * METAR 데이터 정기 수집 및 전송
     * application.yml의 aviation.weather.schedule.metar-cron 설정에 따라 실행
     */
    @Scheduled(cron = "#{@aviationWeatherProperties.schedule.metarCron}")
    public void collectAndSendMetarData() {
        long startTime = System.currentTimeMillis();
        metarExecutionCount++;
        
        logger.info("=== METAR 데이터 정기 수집 시작 ({}회차) ===", metarExecutionCount);
        
        try {
            // METAR 데이터 수집
            CompletableFuture<List<MetarData>> metarFuture = aviationWeatherService.collectMetarData();
            
            // 비동기 처리
            metarFuture.whenComplete((metarDataList, throwable) -> {
                if (throwable != null) {
                    logger.error("METAR 데이터 수집 중 오류 발생", throwable);
                } else {
                    // 카프카로 전송
                    kafkaProducerService.sendMetarData(metarDataList);
                    
                    long executionTime = System.currentTimeMillis() - startTime;
                    lastMetarExecutionTime = executionTime;
                    
                    logger.info("METAR 데이터 처리 완료 - 수집: {}건, 소요시간: {}ms", 
                        metarDataList.size(), executionTime);
                }
            });
            
        } catch (Exception e) {
            logger.error("METAR 스케줄 실행 중 예외 발생", e);
        }
    }
    
    /**
     * PIREP 데이터 정기 수집 및 전송
     * application.properties의 aviation.weather.schedule.pirep-cron 설정에 따라 실행
     */
    @Scheduled(cron = "#{@aviationWeatherProperties.schedule.pirepCron}")
    public void collectAndSendPirepData() {
        long startTime = System.currentTimeMillis();
        pirepExecutionCount++;
        
        logger.info("=== PIREP 데이터 정기 수집 시작 ({}회차) ===", pirepExecutionCount);
        
        try {
            // PIREP 데이터 수집
            CompletableFuture<List<PirepData>> pirepFuture = aviationWeatherService.collectPirepData();
            
            // 비동기 처리
            pirepFuture.whenComplete((pirepDataList, throwable) -> {
                if (throwable != null) {
                    logger.error("PIREP 데이터 수집 중 오류 발생", throwable);
                } else {
                    // 카프카로 전송
                    kafkaProducerService.sendPirepData(pirepDataList);
                    
                    long executionTime = System.currentTimeMillis() - startTime;
                    lastPirepExecutionTime = executionTime;
                    
                    logger.info("PIREP 데이터 처리 완료 - 수집: {}건, 소요시간: {}ms", 
                        pirepDataList.size(), executionTime);
                }
            });
            
        } catch (Exception e) {
            logger.error("PIREP 스케줄 실행 중 예외 발생", e);
        }
    }
    
    /**
     * SIGMET 데이터 정기 수집 및 전송
     * application.yml의 aviation.weather.schedule.sigmet-cron 설정에 따라 실행
     */
    @Scheduled(cron = "#{@aviationWeatherProperties.schedule.sigmetCron}")
    public void collectAndSendSigmetData() {
        long startTime = System.currentTimeMillis();
        sigmetExecutionCount++;
        
        logger.info("=== SIGMET 데이터 정기 수집 시작 ({}회차) ===", sigmetExecutionCount);
        
        try {
            // SIGMET 데이터 수집
            CompletableFuture<List<HazardWeatherData>> sigmetFuture = aviationWeatherService.collectSigmetData();
            
            // 비동기 처리
            sigmetFuture.whenComplete((sigmetDataList, throwable) -> {
                if (throwable != null) {
                    logger.error("SIGMET 데이터 수집 중 오류 발생", throwable);
                } else {
                    // 카프카로 전송
                    kafkaProducerService.sendSigmetData(sigmetDataList);
                    
                    long executionTime = System.currentTimeMillis() - startTime;
                    lastSigmetExecutionTime = executionTime;
                    
                    logger.info("SIGMET 데이터 처리 완료 - 수집: {}건, 소요시간: {}ms", 
                        sigmetDataList.size(), executionTime);
                }
            });
            
        } catch (Exception e) {
            logger.error("SIGMET 스케줄 실행 중 예외 발생", e);
        }
    }
    
    /**
     * 시스템 상태 점검 (매시간 실행)
     * 실행 통계 록
     */
    @Scheduled(cron = "0 0 * * * *")  // 매시간 정각
    public void performHealthCheck() {
        logger.info("=== 시스템 상태 점검 시작 ===");
        
        try {
            // 실행 통계 로그
            logger.info("실행 통계 - METAR: {}회 (최근 {}ms), PIREP: {}회 (최근 {}ms), SIGMET: {}회 (최근 {}ms)",
                metarExecutionCount, lastMetarExecutionTime,
                pirepExecutionCount, lastPirepExecutionTime,
                sigmetExecutionCount, lastSigmetExecutionTime);
                
        } catch (Exception e) {
            logger.error("시스템 상태 점검 중 오류 발생", e);
        }
    }
    
    /**
     * 애플리케이션 시작 시 초기 데이터 수집 (선택적)
     * 서버 시작 후 30초 후에 한 번 실행
     */
    @Scheduled(fixedDelay = Long.MAX_VALUE, initialDelay = 30000)
    public void performInitialDataCollection() {
        logger.info("=== 애플리케이션 시작 후 초기 데이터 수집 ===");
        
        try {
            // 모든 데이터 타입을 순차적으로 한 번씩 수집
            logger.info("초기 METAR 데이터 수집 시작");
            collectAndSendMetarData();
            
            // 5초 대기
            Thread.sleep(5000);
            
            logger.info("초기 PIREP 데이터 수집 시작");
            collectAndSendPirepData();
            
            // 5초 대기
            Thread.sleep(5000);
            
            logger.info("초기 SIGMET 데이터 수집 시작");
            collectAndSendSigmetData();
            
            logger.info("초기 데이터 수집 완료");
            
        } catch (Exception e) {
            logger.error("초기 데이터 수집 중 오류 발생", e);
        }
    }
    
    /**
     * 수동으로 METAR 데이터 수집 트리거
     */
    public void triggerMetarCollection() {
        logger.info("수동 METAR 데이터 수집 트리거");
        collectAndSendMetarData();
    }
    
    /**
     * 수동으로 PIREP 데이터 수집 트리거
     */
    public void triggerPirepCollection() {
        logger.info("수동 PIREP 데이터 수집 트리거");
        collectAndSendPirepData();
    }
    
    /**
     * 수동으로 SIGMET 데이터 수집 트리거
     */
    public void triggerSigmetCollection() {
        logger.info("수동 SIGMET 데이터 수집 트리거");
        collectAndSendSigmetData();
    }
    
    /**
     * 모든 데이터 타입을 수동으로 한 번에 수집
     */
    public void triggerAllDataCollection() {
        logger.info("수동 전체 데이터 수집 트리거");
        
        triggerMetarCollection();
        triggerPirepCollection();
        triggerSigmetCollection();
    }
    
    /**
     * 스케줄러 통계 정보 반환
     */
    public String getSchedulerStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("스케줄러 실행 통계:\n");
        stats.append("- METAR 수집: ").append(metarExecutionCount).append("회 (최근 소요시간: ").append(lastMetarExecutionTime).append("ms)\n");
        stats.append("- PIREP 수집: ").append(pirepExecutionCount).append("회 (최근 소요시간: ").append(lastPirepExecutionTime).append("ms)\n");
        stats.append("- SIGMET 수집: ").append(sigmetExecutionCount).append("회 (최근 소요시간: ").append(lastSigmetExecutionTime).append("ms)\n");
        stats.append("\n스케줄 설정:\n");
        stats.append("- METAR Cron: ").append(properties.getSchedule().getMetarCron()).append("\n");
        stats.append("- PIREP Cron: ").append(properties.getSchedule().getPirepCron()).append("\n");
        stats.append("- SIGMET Cron: ").append(properties.getSchedule().getSigmetCron());
        
        return stats.toString();
    }
}
