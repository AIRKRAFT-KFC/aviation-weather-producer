# Aviation Weather Kafka Producer

항공 기상 데이터(METAR, PIREP, SIGMET)를 자동으로 수집하여 카프카 토픽으로 전송하는 백그라운드 프로듀서 애플리케이션입니다.

## 📋 주요 기능

- **METAR 데이터**: 공항 실시간 기상 관측 데이터 → `AWC-METAR` 토픽 (15분마다)
- **PIREP 데이터**: 파일럿 리포트 데이터 → `AWC-PIREP` 토픽 (20분마다)
- **SIGMET 데이터**: 위험기상 경보 데이터 → `AWC-SIGMET` 토픽 (30분마다)
- **자동 스케줄링**: 설정 가능한 주기로 백그라운드에서 자동 데이터 수집
- **JSON 형식**: Aviation Weather API에서 JSON으로 직접 수집하여 카프카로 전송

## 📁 프로젝트 구조

### 🏠 **루트 디렉토리**
```
aviation-weather-producer/
├── build.gradle              # 의존성, 빌드 설정
├── gradlew / gradlew.bat     # Gradle 실행 파일 (OS별)
├── README.md                 # 이 문서
└── gradle/wrapper/           # Gradle 래퍼 설정
```

### ☕ **Java 소스 코드**
```
src/main/java/com/aviation/weather/
├── AviationWeatherProducerApplication.java  # 🚀 메인 애플리케이션 (시작점)
│
├── config/
│   ├── AviationWeatherProperties.java       # ⚙️ application.properties 설정 바인딩
│   └── KafkaConfig.java                     # 📡 카프카 프로듀서 연결 설정
│
├── model/
│   ├── MetarData.java                       # 📊 METAR 데이터 구조 정의
│   ├── PirepData.java                       # 📊 PIREP 데이터 구조 정의
│   └── HazardWeatherData.java               # 📊 SIGMET 데이터 구조 정의
│
└── service/
    ├── AviationWeatherService.java          # 🌐 Aviation Weather API에서 데이터 수집
    ├── AviationWeatherKafkaProducerService.java # 📤 수집된 데이터를 카프카 토픽으로 전송
    └── AviationWeatherSchedulerService.java # ⏰ 자동 스케줄링 (15분/20분/30분마다 실행)
```

### 📄 **설정 파일**
```
src/main/resources/
└── application.properties    # 🔧 모든 설정 (카프카 주소, 토픽명, 스케줄 등)
```

### 🔄 **실행 흐름**
```
1. AviationWeatherProducerApplication.java → 애플리케이션 시작
2. AviationWeatherProperties.java → application.properties 설정 로드
3. AviationWeatherSchedulerService.java → 스케줄러 시작 및 자동 실행
4. AviationWeatherService.java → Aviation Weather API에서 데이터 수집
5. AviationWeatherKafkaProducerService.java → 수집된 데이터를 카프카 토픽으로 전송
6. 스케줄에 따라 자동 반복...
```

## 🚀 실행 방법

### 1. 카프카 서버 실행
카프카가 실행 중이어야 합니다.

### 2. 애플리케이션 실행
```bash
# 프로젝트 디렉토리로 이동
cd C:\Users\r2com\Desktop\kafka\aviation-weather-producer

# 애플리케이션 실행 (백그라운드에서 자동 실행)
.\gradlew.bat bootRun
```

### 3. 실행 확인
애플리케이션이 시작되면 자동으로:
- 15분마다 METAR 데이터 수집 → AWC-METAR 토픽
- 20분마다 PIREP 데이터 수집 → AWC-PIREP 토픽  
- 30분마다 SIGMET 데이터 수집 → AWC-SIGMET 토픽

## ⚙️ 설정 커스터마이징

### application.properties에서 설정 밀경 가능한 항목:

```properties
# 카프카 서버 변경
spring.kafka.bootstrap-servers=your-kafka-server:9092

# 수집 주기 변경
aviation.weather.schedule.metar-cron=0 */10 * * * *   # 10분마다
aviation.weather.schedule.pirep-cron=0 */30 * * * *   # 30분마다
aviation.weather.schedule.sigmet-cron=0 */45 * * * *  # 45분마다

# 토픽명 변경
aviation.weather.topics.metar=your-metar-topic-name
aviation.weather.topics.pirep=your-pirep-topic-name
aviation.weather.topics.sigmet=your-sigmet-topic-name

# API 설정
aviation.weather.request-timeout-seconds=45
aviation.weather.retry-attempts=5
```

## 📊 데이터 구조

### METAR 토픽 데이터 예시:
```json
{
  "dataType": "METAR",
  "collectionTimestamp": "2025-05-30T14:30:00",
  "observationTime": "2025-05-30T14:00:00",
  "airport": {
    "icaoCode": "RKSI",
    "name": "인천국제공항",
    "latitude": 37.4602,
    "longitude": 126.4407
  },
  "weather": {
    "wind": {
      "direction": 250,
      "speed": 8
    },
    "visibility": {
      "distance": 10.0,
      "unit": "SM"
    },
    "temperature": 24.0,
    "dewpoint": 18.0,
    "altimeter": {
      "setting": 30.12,
      "unit": "inHg"
    }
  },
  "rawData": "RKSI 301400Z 25008KT 10SM FEW020 SCT100 24/18 A3012",
  "source": "Aviation Weather Center"
}
```

### PIREP 토픽 데이터 예시:
```json
{
  "dataType": "PIREP",
  "collectionTimestamp": "2025-05-30T14:30:00",
  "reportTime": "2025-05-30T14:15:00",
  "aircraft": {
    "aircraftType": "B737"
  },
  "location": {
    "latitude": 37.5,
    "longitude": 126.9,
    "altitude": 35000
  },
  "weather": {
    "turbulence": {
      "intensity": "LIGHT",
      "type": "CHOP"
    },
    "temperature": -45
  },
  "rawData": "UA /OV RKSI090030 /TM 1415 /FL350 /TP B737 /TB LGT CHOP /TA M45",
  "source": "Aviation Weather Center"
}
```

### SIGMET 토픽 데이터 예시:
```json
{
  "dataType": "SIGMET",
  "collectionTimestamp": "2025-05-30T14:30:00",
  "issueTime": "2025-05-30T14:00:00",
  "validFrom": "2025-05-30T15:00:00",
  "validTo": "2025-05-30T21:00:00",
  "hazardType": "TURB",
  "severityLevel": "MODERATE",
  "area": {
    "description": "서울 FIR"
  },
  "altitude": {
    "minimumFeet": 25000,
    "maximumFeet": 45000
  },
  "rawData": "RKRR SIGMET A1 VALID 301500/302100 RKSI- SEOUL FIR MOD TURB FL250/450",
  "source": "Aviation Weather Center"
}
```

## 📈 로그 확인

로그 파일: `logs/aviation-weather-producer.log`

콘솔에서 다음과 같은 로그를 확인할 수 있습니다:
```
2025-05-30 14:15:00 [scheduling-1] INFO  c.a.w.s.AviationWeatherSchedulerService - === METAR 데이터 정기 수집 시작 (1회차) ===
2025-05-30 14:15:05 [scheduling-1] INFO  c.a.w.s.AviationWeatherService - METAR 데이터 수집 완료: 150 건
2025-05-30 14:15:06 [scheduling-1] INFO  c.a.w.s.AviationWeatherKafkaProducerService - METAR 데이터 카프카 전송 완료 - 성공: 150, 실패: 0
```

## 🎆 축하합니다!

**항공 관제용 실시간 기상 데이터 파이프라인이 완성되었습니다!** 🛩️✨

- ✅ 자동으로 데이터 수집 및 전송
- ✅ JSON 형식으로 깨끗하게 처리
- ✅ 안정적인 스케줄링
- ✅ 설정 파일로 쉽게 커스터마이징

이제 Aviation Weather API에서 수집된 데이터가 자동으로 카프카 토픽으로 전송됩니다!
