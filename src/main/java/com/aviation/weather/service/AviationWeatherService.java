package com.aviation.weather.service;

import com.aviation.weather.config.AviationWeatherProperties;
import com.aviation.weather.model.MetarData;
import com.aviation.weather.model.HazardWeatherData;
import com.aviation.weather.model.PirepData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 항공 기상 데이터 수집 서비스
 * METAR, PIREP, SIGMET 데이터를 수집하고 JSON으로 변환
 */
@Service
public class AviationWeatherService {

    private static final Logger logger = LoggerFactory.getLogger(AviationWeatherService.class);

    private final WebClient webClient;
    private final AviationWeatherProperties properties;
    private final ObjectMapper objectMapper;

    // API 엔드포인트 상수 (실제 Aviation Weather API 엔드포인트)
    private static final String METAR_ENDPOINT = "/metar";
    private static final String PIREP_ENDPOINT = "/pirep";
    private static final String SIGMET_ENDPOINT = "/airsigmet";

    @Autowired
    public AviationWeatherService(WebClient webClient,
                                  AviationWeatherProperties properties,
                                  ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;

        // 설정된 WebClient 사용 (Configuration에서 주입받음)
        // 코덱 설정을 유지하면서 추가 설정만 적용
        this.webClient = webClient.mutate()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader("User-Agent", properties.getUserAgent())
                .defaultHeader("Accept", "application/xml,application/json")
                .codecs(configurer -> {
                    // 버퍼 크기를 20MB로 설정하여 대용량 응답 처리
                    configurer.defaultCodecs().maxInMemorySize(20 * 1024 * 1024); // 20MB
                })
                .build();
    }

    /**
     * METAR 데이터 수집 → AWC-METAR 토픽
     */
    public CompletableFuture<List<MetarData>> collectMetarData() {
        logger.info("METAR 데이터 수집 시작 - 전체 데이터");

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(METAR_ENDPOINT)
                        .queryParam("format", "json")
                        .queryParam("hours", properties.getFilters().getMetarHoursBack())
                        .queryParam("mostRecentForEachStation", "true")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(properties.getRetryAttempts(), Duration.ofSeconds(2)))
                .timeout(Duration.ofSeconds(properties.getRequestTimeoutSeconds()))
                .map(jsonData -> {
                    return parseMetarJsonResponse(jsonData);
                })
                .doOnSuccess(data -> logger.info("METAR 데이터 수집 완료: {} 건", data.size()))
                .doOnError(this::logError)
                .onErrorReturn(new ArrayList<>())
                .toFuture();
    }

    /**
     * PIREP 데이터 수집 → AWC-PIREP 토픽
     */
    public CompletableFuture<List<PirepData>> collectPirepData() {
        logger.info("PIREP 데이터 수집 시작");

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PIREP_ENDPOINT)
                        .queryParam("format", "json")
                        .queryParam("hours", 6)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(properties.getRetryAttempts(), Duration.ofSeconds(2)))
                .timeout(Duration.ofSeconds(properties.getRequestTimeoutSeconds()))
                .map(jsonData -> {
                    return parsePirepJsonResponse(jsonData);
                })
                .doOnSuccess(data -> logger.info("PIREP 데이터 수집 완료: {} 건", data.size()))
                .doOnError(this::logError)
                .onErrorReturn(new ArrayList<>())
                .toFuture();
    }

    /**
     * SIGMET 데이터 수집 → AWC-SIGMET 토픽
     */
    public CompletableFuture<List<HazardWeatherData>> collectSigmetData() {
        logger.info("SIGMET 데이터 수집 시작");

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SIGMET_ENDPOINT)
                        .queryParam("format", "json")
                        .queryParam("date", "current")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(properties.getRetryAttempts(), Duration.ofSeconds(2)))
                .timeout(Duration.ofSeconds(properties.getRequestTimeoutSeconds()))
                .map(jsonData -> parseHazardWeatherJsonResponse(jsonData, "SIGMET"))
                .doOnSuccess(data -> logger.info("SIGMET 데이터 수집 완료: {} 건", data.size()))
                .doOnError(this::logError)
                .onErrorReturn(new ArrayList<>())
                .toFuture();
    }

    // === 파싱 메서드들 ===

    private List<MetarData> parseMetarJsonResponse(String jsonResponse) {
        List<MetarData> metarDataList = new ArrayList<>();

        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            // API 응답이 직접 배열인 경우
            if (rootNode.isArray()) {
                for (JsonNode dataNode : rootNode) {
                    MetarData metarData = parseMetarDataNode(dataNode);
                    if (metarData != null) {
                        metarDataList.add(metarData);
                    }
                }
            } else {
                logger.warn("예상과 다른 METAR JSON 구조: {}", jsonResponse.substring(0, Math.min(200, jsonResponse.length())));
            }

        } catch (Exception e) {
            logger.error("METAR JSON 파싱 오류", e);
        }

        return metarDataList;
    }

    private List<PirepData> parsePirepJsonResponse(String jsonResponse) {
        List<PirepData> pirepDataList = new ArrayList<>();

        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            // API 응답이 직접 배열인 경우 (실제 API 구조에 맞춤)
            if (rootNode.isArray()) {
                for (JsonNode dataNode : rootNode) {
                    PirepData pirepData = parsePirepDataNode(dataNode);
                    if (pirepData != null) {
                        pirepDataList.add(pirepData);
                    }
                }
            } else {
                logger.warn("예상과 다른 PIREP JSON 구조: {}", jsonResponse.substring(0, Math.min(200, jsonResponse.length())));
            }

        } catch (Exception e) {
            logger.error("PIREP JSON 파싱 오류", e);
        }

        return pirepDataList;
    }

    private List<HazardWeatherData> parseHazardWeatherJsonResponse(String jsonResponse, String dataType) {
        List<HazardWeatherData> hazardDataList = new ArrayList<>();

        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode dataArray = findDataArray(rootNode);

            if (dataArray != null && dataArray.isArray()) {
                for (JsonNode dataNode : dataArray) {
                    HazardWeatherData hazardData = parseHazardWeatherDataNode(dataNode, dataType);
                    if (hazardData != null) {
                        hazardDataList.add(hazardData);
                    }
                }
            }

        } catch (Exception e) {
            logger.error("{} JSON 파싱 오류", dataType, e);
        }

        return hazardDataList;
    }

    // === 개별 데이터 파싱 메서드들 ===

    private MetarData parseMetarDataNode(JsonNode dataNode) {
        try {
            MetarData metarData = new MetarData();
            metarData.setCollectionTimestamp(LocalDateTime.now());

            // 실제 API 응답 필드에 정확히 매핑
            metarData.setIcaoCode(getTextValue(dataNode, "icaoId"));  // icaoId
            metarData.setAirportName(getTextValue(dataNode, "name"));   // name
            metarData.setLatitude(getDoubleValue(dataNode, "lat"));     // lat
            metarData.setLongitude(getDoubleValue(dataNode, "lon"));    // lon
            metarData.setElevation(getIntValue(dataNode, "elev"));      // elev

            // 기상 데이터 - 정확한 필드명 사용
            metarData.setTemperature(getDoubleValue(dataNode, "temp"));    // temp
            metarData.setDewpoint(getDoubleValue(dataNode, "dewp"));       // dewp
            metarData.setWindDirection(getIntValue(dataNode, "wdir"));     // wdir
            metarData.setWindSpeed(getIntValue(dataNode, "wspd"));         // wspd
            metarData.setWindGust(getIntValue(dataNode, "wgst"));          // wgst
            metarData.setVisibility(getTextValue(dataNode, "visib"));      // visib
            metarData.setAltimeter(getIntValue(dataNode, "altim"));        // altim
            metarData.setSeaLevelPressure(getDoubleValue(dataNode, "slp")); // slp
            metarData.setWeatherPhenomena(getTextValue(dataNode, "wxString")); // wxString

            // 원시 관측 데이터
            metarData.setRawObservation(getTextValue(dataNode, "rawOb"));

            // 관측 시각 파싱
            String reportTimeStr = getTextValue(dataNode, "reportTime");
            if (reportTimeStr != null) {
                try {
                    metarData.setObservationTime(LocalDateTime.parse(reportTimeStr.replace(" ", "T")));
                } catch (Exception e) {
                    // 시간 파싱 실패 시 생략
                }
            }

            // 구름 정보 파싱 - clouds 배열
            JsonNode cloudsNode = dataNode.get("clouds");
            if (cloudsNode != null && cloudsNode.isArray()) {
                List<MetarData.CloudInfo> clouds = new ArrayList<>();
                for (JsonNode cloudNode : cloudsNode) {
                    MetarData.CloudInfo cloud = new MetarData.CloudInfo();
                    cloud.setCoverage(getTextValue(cloudNode, "cover"));
                    cloud.setAltitude(getIntValue(cloudNode, "base"));
                    clouds.add(cloud);
                }
                metarData.setClouds(clouds);
            }

            return metarData;

        } catch (Exception e) {
            logger.error("METAR 데이터 노드 파싱 실패", e);
            return null;
        }
    }

    /**
     * PIREP 데이터 노드 파싱 - 간소화된 버전
     * 필요한 15개 필드만 추출
     */
    private PirepData parsePirepDataNode(JsonNode dataNode) {
        try {
            PirepData pirepData = new PirepData();

            // 실제 API 응답 필드명으로 직접 매핑
            pirepData.setPirepId(getLongValue(dataNode, "pirepId"));
            pirepData.setReceiptTime(getTextValue(dataNode, "receiptTime"));
            pirepData.setObsTime(getLongValue(dataNode, "obsTime"));
            pirepData.setIcaoId(getTextValue(dataNode, "icaoId"));
            pirepData.setAcType(getTextValue(dataNode, "acType"));
            pirepData.setLatitude(getDoubleValue(dataNode, "lat"));
            pirepData.setLongitude(getDoubleValue(dataNode, "lon"));
            pirepData.setFlightLevel(getTextValue(dataNode, "fltLvl"));
            pirepData.setFlightLevelType(getTextValue(dataNode, "fltLvlType"));
            pirepData.setTemperature(getIntValue(dataNode, "temp"));
            pirepData.setWindDirection(getIntValue(dataNode, "wdir"));
            pirepData.setWindSpeed(getIntValue(dataNode, "wspd"));
            pirepData.setPirepType(getTextValue(dataNode, "pirepType"));
            pirepData.setRawObservation(getTextValue(dataNode, "rawOb"));

            return pirepData;

        } catch (Exception e) {
            logger.error("PIREP 데이터 노드 파싱 실패", e);
            return null;
        }
    }

    private HazardWeatherData parseHazardWeatherDataNode(JsonNode dataNode, String dataType) {
        try {
            HazardWeatherData hazardData = new HazardWeatherData();
            hazardData.setDataType(dataType);
            hazardData.setCollectionTimestamp(LocalDateTime.now());

            // 실제 API 응답 필드에 맞춰 매핑
            hazardData.setAirSigmetId(getTextValue(dataNode, "airSigmetId"));
            hazardData.setIcaoId(getTextValue(dataNode, "icaoId"));
            hazardData.setHazard(getTextValue(dataNode, "hazard"));
            hazardData.setSeverity(getIntValue(dataNode, "severity")); // 숫자로 받음

            // 고도 정보
            hazardData.setAltitudeHi1(getIntValue(dataNode, "altitudeHi1"));
            hazardData.setAltitudeHi2(getIntValue(dataNode, "altitudeHi2"));

            // 이동 정보
            hazardData.setMovementDir(getIntValue(dataNode, "movementDir"));
            hazardData.setMovementSpd(getIntValue(dataNode, "movementSpd"));

            // 좌표 정보
            hazardData.setCoord(getTextValue(dataNode, "coord"));
            
            // 추가 필드들
            hazardData.setRawAirSigmet(getTextValue(dataNode, "rawAirSigmet"));
            hazardData.setAlphaChar(getTextValue(dataNode, "alphaChar"));
            hazardData.setReceiptTime(getTextValue(dataNode, "receiptTime"));
            hazardData.setAirSigmetType(getTextValue(dataNode, "airSigmetType"));

            // === Unix timestamp 처리 ===
            
            // 유효 시간 범위 파싱 (Unix timestamp)
            hazardData.setValidTimeFrom(getLongValue(dataNode, "validTimeFrom"));
            hazardData.setValidTimeTo(getLongValue(dataNode, "validTimeTo"));

            // 생성 시간 파싱 ("2025-05-30 06:55:00" 형식)
            String creationTimeStr = getTextValue(dataNode, "creationTime");
            if (creationTimeStr != null) {
                try {
                    hazardData.setCreationTime(LocalDateTime.parse(creationTimeStr.replace(" ", "T")));
                } catch (Exception e) {
                    // 시간 파싱 실패 시 생략
                }
            }

            return hazardData;

        } catch (Exception e) {
            logger.error("{} 데이터 노드 파싱 실패", dataType, e);
            return null;
        }
    }

    // === 유틸리티 메서드들 ===

    private JsonNode findDataArray(JsonNode rootNode) {
        if (rootNode.has("response") && rootNode.get("response").has("data")) {
            return rootNode.get("response").get("data");
        } else if (rootNode.has("data")) {
            return rootNode.get("data");
        } else if (rootNode.isArray()) {
            return rootNode;
        }
        return null;
    }

    private String getTextValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode != null && !fieldNode.isNull() ? fieldNode.asText() : null;
    }

    private Integer getIntValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        if (fieldNode != null && !fieldNode.isNull() && fieldNode.canConvertToInt()) {
            return fieldNode.asInt();
        }
        return null;
    }

    private Long getLongValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        if (fieldNode != null && !fieldNode.isNull() && (fieldNode.isNumber() || fieldNode.canConvertToLong())) {
            return fieldNode.asLong();
        }
        return null;
    }

    private Double getDoubleValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        if (fieldNode != null && !fieldNode.isNull() && (fieldNode.isNumber() || fieldNode.isTextual())) {
            try {
                return fieldNode.asDouble();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private void logError(Throwable error) {
        if (error instanceof WebClientResponseException) {
            WebClientResponseException webError = (WebClientResponseException) error;
            logger.error("API 호출 실패 - Status: {}", webError.getStatusCode());
        } else {
            logger.error("데이터 수집 실패", error);
        }
    }
}