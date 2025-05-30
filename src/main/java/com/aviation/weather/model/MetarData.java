package com.aviation.weather.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

/**
 * METAR 데이터 모델 - API 응답에 맞게 단순화
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetarData {

    @JsonProperty("dataType")
    private String dataType = "METAR";

    @JsonProperty("collectionTimestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime collectionTimestamp;

    @JsonProperty("observationTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime observationTime;

    @JsonProperty("icaoCode")
    private String icaoCode;

    @JsonProperty("name")  // API 필드명으로 변경
    private String airportName;

    @JsonProperty("lat")   // API 필드명으로 변경
    private Double latitude;

    @JsonProperty("lon")   // API 필드명으로 변경
    private Double longitude;

    @JsonProperty("elev")  // API 필드명으로 변경
    private Integer elevation;

    @JsonProperty("temp")  // API 필드명으로 변경
    private Double temperature;  // 기온 (섭씨)

    @JsonProperty("dewp")  // API 필드명으로 변경
    private Double dewpoint;     // 이슬점 (섭씨)

    @JsonProperty("wdir")  // API 필드명으로 변경
    private Integer windDirection; // 풍향 (도)

    @JsonProperty("wspd")  // API 필드명으로 변경
    private Integer windSpeed;     // 풍속 (knots)

    @JsonProperty("windGust")
    private Integer windGust;      // 돌풍 (knots)

    @JsonProperty("visib") // API 필드명으로 변경
    private String visibility;     // 시정

    @JsonProperty("altim") // API 필드명으로 변경
    private Integer altimeter;     // 기압 고도계 설정

    @JsonProperty("slp")   // API 필드명으로 변경
    private Double seaLevelPressure; // 해수면 기압

    @JsonProperty("wxString") // API 필드명으로 변경
    private String weatherPhenomena; // 기상현상

    @JsonProperty("clouds")
    private List<CloudInfo> clouds; // 구름 정보

    @JsonProperty("rawObservation")
    private String rawObservation;  // 원시 METAR 문자열

    @JsonProperty("source")
    private String source = "Aviation Weather Center";

    // 구름 정보 클래스
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CloudInfo {
        @JsonProperty("coverage")
        private String coverage;    // SCT, BKN, OVC 등

        @JsonProperty("altitude")
        private Integer altitude;   // 구름 고도 (feet)

        // Getters and Setters
        public String getCoverage() { return coverage; }
        public void setCoverage(String coverage) { this.coverage = coverage; }

        public Integer getAltitude() { return altitude; }
        public void setAltitude(Integer altitude) { this.altitude = altitude; }
    }

    // Getters and Setters
    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }

    public LocalDateTime getCollectionTimestamp() { return collectionTimestamp; }
    public void setCollectionTimestamp(LocalDateTime collectionTimestamp) { this.collectionTimestamp = collectionTimestamp; }

    public LocalDateTime getObservationTime() { return observationTime; }
    public void setObservationTime(LocalDateTime observationTime) { this.observationTime = observationTime; }

    public String getIcaoCode() { return icaoCode; }
    public void setIcaoCode(String icaoCode) { this.icaoCode = icaoCode; }

    public String getAirportName() { return airportName; }
    public void setAirportName(String airportName) { this.airportName = airportName; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Integer getElevation() { return elevation; }
    public void setElevation(Integer elevation) { this.elevation = elevation; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Double getDewpoint() { return dewpoint; }
    public void setDewpoint(Double dewpoint) { this.dewpoint = dewpoint; }

    public Integer getWindDirection() { return windDirection; }
    public void setWindDirection(Integer windDirection) { this.windDirection = windDirection; }

    public Integer getWindSpeed() { return windSpeed; }
    public void setWindSpeed(Integer windSpeed) { this.windSpeed = windSpeed; }

    public Integer getWindGust() { return windGust; }
    public void setWindGust(Integer windGust) { this.windGust = windGust; }

    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }

    public Integer getAltimeter() { return altimeter; }
    public void setAltimeter(Integer altimeter) { this.altimeter = altimeter; }

    public Double getSeaLevelPressure() { return seaLevelPressure; }
    public void setSeaLevelPressure(Double seaLevelPressure) { this.seaLevelPressure = seaLevelPressure; }

    public String getWeatherPhenomena() { return weatherPhenomena; }
    public void setWeatherPhenomena(String weatherPhenomena) { this.weatherPhenomena = weatherPhenomena; }

    public List<CloudInfo> getClouds() { return clouds; }
    public void setClouds(List<CloudInfo> clouds) { this.clouds = clouds; }

    public String getRawObservation() { return rawObservation; }
    public void setRawObservation(String rawObservation) { this.rawObservation = rawObservation; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}