package com.aviation.weather.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * 항공 기상 API 설정 프로퍼티
 * application.yml에서 aviation.weather 하위 설정을 바인딩
 */
@Component
@ConfigurationProperties(prefix = "aviation.weather")
@Validated
public class AviationWeatherProperties {
    
    /**
     * API 기본 URL
     */
    @NotBlank
    private String baseUrl = "https://aviationweather.gov/api/data";
    
    /**
     * User-Agent 헤더
     */
    @NotBlank
    private String userAgent = "AviationWeatherProducer/1.0";
    
    /**
     * HTTP 요청 타임아웃 (초)
     */
    @Positive
    private int requestTimeoutSeconds = 30;
    
    /**
     * 재시도 횟수
     */
    @Positive
    private int retryAttempts = 3;
    
    /**
     * 수집할 공항 코드 목록 (ICAO 코드) - 선택 사항
     */
    private List<String> airportCodes;
    
    /**
     * 스케줄 설정
     */
    private Schedule schedule = new Schedule();
    
    /**
     * 카프카 토픽 설정
     */
    private Topics topics = new Topics();
    
    /**
     * 데이터 필터링 설정
     */
    private Filters filters = new Filters();
    
    /**
     * XML to JSON 변환 설정
     */
    private XmlConversion xmlConversion = new XmlConversion();
    
    // === 내부 클래스들 ===
    
    public static class Schedule {
        private String metarCron = "0 */15 * * * *";      // 15분마다
        private String pirepCron = "0 */20 * * * *";      // 20분마다
        private String sigmetCron = "0 */30 * * * *";     // 30분마다
        
        // Getters and Setters
        public String getMetarCron() { return metarCron; }
        public void setMetarCron(String metarCron) { this.metarCron = metarCron; }
        
        public String getPirepCron() { return pirepCron; }
        public void setPirepCron(String pirepCron) { this.pirepCron = pirepCron; }
        
        public String getSigmetCron() { return sigmetCron; }
        public void setSigmetCron(String sigmetCron) { this.sigmetCron = sigmetCron; }
    }
    
    public static class Topics {
        private String metar = "AWC-METAR";
        private String pirep = "AWC-PIREP";
        private String sigmet = "AWC-SIGMET";
        
        // Getters and Setters
        public String getMetar() { return metar; }
        public void setMetar(String metar) { this.metar = metar; }
        
        public String getPirep() { return pirep; }
        public void setPirep(String pirep) { this.pirep = pirep; }
        
        public String getSigmet() { return sigmet; }
        public void setSigmet(String sigmet) { this.sigmet = sigmet; }
    }
    
    public static class Filters {
        private int metarHoursBack = 2;
        private List<String> hazardRegions = List.of("all");
        private List<String> hazardTypes = List.of("all", "conv", "turb", "ice", "mtw", "sand", "trop", "ash");
        
        // Getters and Setters
        public int getMetarHoursBack() { return metarHoursBack; }
        public void setMetarHoursBack(int metarHoursBack) { this.metarHoursBack = metarHoursBack; }
        
        public List<String> getHazardRegions() { return hazardRegions; }
        public void setHazardRegions(List<String> hazardRegions) { this.hazardRegions = hazardRegions; }
        
        public List<String> getHazardTypes() { return hazardTypes; }
        public void setHazardTypes(List<String> hazardTypes) { this.hazardTypes = hazardTypes; }
    }
    
    public static class XmlConversion {
        private boolean enabled = true;
        private boolean prettyPrint = false;
        private boolean includeXmlAttributes = true;
        private String rootElementName = "weatherData";
        
        // Getters and Setters
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        
        public boolean isPrettyPrint() { return prettyPrint; }
        public void setPrettyPrint(boolean prettyPrint) { this.prettyPrint = prettyPrint; }
        
        public boolean isIncludeXmlAttributes() { return includeXmlAttributes; }
        public void setIncludeXmlAttributes(boolean includeXmlAttributes) { this.includeXmlAttributes = includeXmlAttributes; }
        
        public String getRootElementName() { return rootElementName; }
        public void setRootElementName(String rootElementName) { this.rootElementName = rootElementName; }
    }
    
    // === 메인 클래스 Getters and Setters ===
    
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    
    public int getRequestTimeoutSeconds() { return requestTimeoutSeconds; }
    public void setRequestTimeoutSeconds(int requestTimeoutSeconds) { this.requestTimeoutSeconds = requestTimeoutSeconds; }
    
    public int getRetryAttempts() { return retryAttempts; }
    public void setRetryAttempts(int retryAttempts) { this.retryAttempts = retryAttempts; }
    
    public List<String> getAirportCodes() { return airportCodes; }
    public void setAirportCodes(List<String> airportCodes) { this.airportCodes = airportCodes; }
    
    public Schedule getSchedule() { return schedule; }
    public void setSchedule(Schedule schedule) { this.schedule = schedule; }
    
    public Topics getTopics() { return topics; }
    public void setTopics(Topics topics) { this.topics = topics; }
    
    public Filters getFilters() { return filters; }
    public void setFilters(Filters filters) { this.filters = filters; }
    
    public XmlConversion getXmlConversion() { return xmlConversion; }
    public void setXmlConversion(XmlConversion xmlConversion) { this.xmlConversion = xmlConversion; }
}
