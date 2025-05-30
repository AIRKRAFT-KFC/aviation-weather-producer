package com.aviation.weather.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * AIRMET/SIGMET 위험기상 경보 데이터 모델 - API 응답에 맞게 단순화
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HazardWeatherData {
    
    @JsonProperty("dataType")
    private String dataType;
    
    @JsonProperty("collectionTimestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime collectionTimestamp;
    
    @JsonProperty("airSigmetId")
    private String airSigmetId;  // airSigmetId
    
    @JsonProperty("icaoId")
    private String icaoId;       // icaoId
    
    @JsonProperty("hazard")
    private String hazard;       // hazard (위험 유형)
    
    @JsonProperty("severity")
    private Integer severity;     // severity (심각도 - 숫자)
    
    @JsonProperty("validTimeFrom")
    private Long validTimeFrom;  // validTimeFrom (Unix timestamp)
    
    @JsonProperty("validTimeTo")
    private Long validTimeTo;    // validTimeTo (Unix timestamp)
    
    @JsonProperty("altitudeHi1")
    private Integer altitudeHi1;  // altitudeHi1 (최소 고도)
    
    @JsonProperty("altitudeHi2")
    private Integer altitudeHi2;  // altitudeHi2 (최대 고도)
    
    @JsonProperty("movementDir")
    private Integer movementDir;  // movementDir (이동 방향)
    
    @JsonProperty("movementSpd")
    private Integer movementSpd;  // movementSpd (이동 속도)
    
    @JsonProperty("coord")
    private String coord;         // coord (좌표)
    
    @JsonProperty("rawAirSigmet")
    private String rawAirSigmet;  // rawAirSigmet (원시 데이터)
    
    @JsonProperty("alphaChar")
    private String alphaChar;     // alphaChar (알파벳 식별자)
    
    @JsonProperty("receiptTime")
    private String receiptTime;   // receiptTime (수신 시간)
    
    @JsonProperty("airSigmetType")
    private String airSigmetType; // airSigmetType (SIGMET/AIRMET)
    
    @JsonProperty("creationTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime creationTime;   // creationTime
    
    @JsonProperty("source")
    private String source = "Aviation Weather Center";
    
    // Getters and Setters
    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }
    
    public LocalDateTime getCollectionTimestamp() { return collectionTimestamp; }
    public void setCollectionTimestamp(LocalDateTime collectionTimestamp) { this.collectionTimestamp = collectionTimestamp; }
    
    public String getAirSigmetId() { return airSigmetId; }
    public void setAirSigmetId(String airSigmetId) { this.airSigmetId = airSigmetId; }
    
    public String getIcaoId() { return icaoId; }
    public void setIcaoId(String icaoId) { this.icaoId = icaoId; }
    
    public String getHazard() { return hazard; }
    public void setHazard(String hazard) { this.hazard = hazard; }
    
    public String getSeverity() { return severity != null ? severity.toString() : null; }
    public void setSeverity(String severity) { 
        try {
            this.severity = severity != null ? Integer.parseInt(severity) : null;
        } catch (NumberFormatException e) {
            this.severity = null;
        }
    }
    public void setSeverity(Integer severity) { this.severity = severity; }
    
    public Long getValidTimeFrom() { return validTimeFrom; }
    public void setValidTimeFrom(Long validTimeFrom) { this.validTimeFrom = validTimeFrom; }
    
    public Long getValidTimeTo() { return validTimeTo; }
    public void setValidTimeTo(Long validTimeTo) { this.validTimeTo = validTimeTo; }
    
    public Integer getAltitudeHi1() { return altitudeHi1; }
    public void setAltitudeHi1(Integer altitudeHi1) { this.altitudeHi1 = altitudeHi1; }
    
    public Integer getAltitudeHi2() { return altitudeHi2; }
    public void setAltitudeHi2(Integer altitudeHi2) { this.altitudeHi2 = altitudeHi2; }
    
    public Integer getMovementDir() { return movementDir; }
    public void setMovementDir(Integer movementDir) { this.movementDir = movementDir; }
    
    public Integer getMovementSpd() { return movementSpd; }
    public void setMovementSpd(Integer movementSpd) { this.movementSpd = movementSpd; }
    
    public String getCoord() { return coord; }
    public void setCoord(String coord) { this.coord = coord; }
    
    public String getRawAirSigmet() { return rawAirSigmet; }
    public void setRawAirSigmet(String rawAirSigmet) { this.rawAirSigmet = rawAirSigmet; }
    
    public String getAlphaChar() { return alphaChar; }
    public void setAlphaChar(String alphaChar) { this.alphaChar = alphaChar; }
    
    public String getReceiptTime() { return receiptTime; }
    public void setReceiptTime(String receiptTime) { this.receiptTime = receiptTime; }
    
    public String getAirSigmetType() { return airSigmetType; }
    public void setAirSigmetType(String airSigmetType) { this.airSigmetType = airSigmetType; }
    
    public LocalDateTime getCreationTime() { return creationTime; }
    public void setCreationTime(LocalDateTime creationTime) { this.creationTime = creationTime; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
