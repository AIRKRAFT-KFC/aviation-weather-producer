package com.aviation.weather.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * PIREP (Pilot Report) 데이터 모델 - 카프카 전송용 간소화 버전
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PirepData {

    /**
     * PIREP 고유 ID
     */
    @JsonProperty("pirepId")
    private Long pirepId;

    /**
     * 수신 시각
     */
    @JsonProperty("receiptTime")
    private String receiptTime;

    /**
     * 관측 시각 (Unix timestamp)
     */
    @JsonProperty("obsTime")
    private Long obsTime;

    /**
     * ICAO 코드
     */
    @JsonProperty("icaoId")
    private String icaoId;

    /**
     * 항공기 타입/콜사인
     */
    @JsonProperty("acType")
    private String acType;

    /**
     * 위도
     */
    @JsonProperty("lat")
    private Double latitude;

    /**
     * 경도
     */
    @JsonProperty("lon")
    private Double longitude;

    /**
     * 비행 고도
     */
    @JsonProperty("fltLvl")
    private String flightLevel;

    /**
     * 비행 고도 타입
     */
    @JsonProperty("fltLvlType")
    private String flightLevelType;

    /**
     * 기온 (섭씨)
     */
    @JsonProperty("temp")
    private Integer temperature;

    /**
     * 풍향 (도)
     */
    @JsonProperty("wdir")
    private Integer windDirection;

    /**
     * 풍속 (노트)
     */
    @JsonProperty("wspd")
    private Integer windSpeed;

    /**
     * PIREP 타입 (PIREP, AIREP 등)
     */
    @JsonProperty("pirepType")
    private String pirepType;

    /**
     * 원시 관측 데이터
     */
    @JsonProperty("rawOb")
    private String rawObservation;

    // Getters and Setters
    public Long getPirepId() { return pirepId; }
    public void setPirepId(Long pirepId) { this.pirepId = pirepId; }

    public String getReceiptTime() { return receiptTime; }
    public void setReceiptTime(String receiptTime) { this.receiptTime = receiptTime; }

    public Long getObsTime() { return obsTime; }
    public void setObsTime(Long obsTime) { this.obsTime = obsTime; }

    public String getIcaoId() { return icaoId; }
    public void setIcaoId(String icaoId) { this.icaoId = icaoId; }

    public String getAcType() { return acType; }
    public void setAcType(String acType) { this.acType = acType; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getFlightLevel() { return flightLevel; }
    public void setFlightLevel(String flightLevel) { this.flightLevel = flightLevel; }

    public String getFlightLevelType() { return flightLevelType; }
    public void setFlightLevelType(String flightLevelType) { this.flightLevelType = flightLevelType; }

    public Integer getTemperature() { return temperature; }
    public void setTemperature(Integer temperature) { this.temperature = temperature; }

    public Integer getWindDirection() { return windDirection; }
    public void setWindDirection(Integer windDirection) { this.windDirection = windDirection; }

    public Integer getWindSpeed() { return windSpeed; }
    public void setWindSpeed(Integer windSpeed) { this.windSpeed = windSpeed; }

    public String getPirepType() { return pirepType; }
    public void setPirepType(String pirepType) { this.pirepType = pirepType; }

    public String getRawObservation() { return rawObservation; }
    public void setRawObservation(String rawObservation) { this.rawObservation = rawObservation; }
}