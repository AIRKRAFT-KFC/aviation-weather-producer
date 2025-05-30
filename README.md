# ✈️ Aviation Weather Kafka Producer

> **완전 초보자도 5분만에 실행 가능한 항공 기상 데이터 수집기** 🌤️

전 세계 750개 공항의 실시간 기상 데이터를 자동으로 수집하여 Kafka로 전송하는 백그라운드 서비스입니다.

## 🎯 이 프로그램이 하는 일

### 📡 **자동 데이터 수집**
- **METAR**: 750개 공항의 실시간 날씨 (기온, 바람, 시정 등)
- **PIREP**: 조종사들이 하늘에서 직접 보고하는 날씨 상황
- **SIGMET**: 항공기에 위험한 기상 경보 (태풍, 난기류, 화산재 등)

### ⏰ **완전 자동화**
- 1시간마다 최신 데이터 자동 수집
- Kafka 토픽으로 실시간 전송
- 24시간 무중단 서비스

### 📊 **깔끔한 JSON 데이터**
```json
{
  "airport": "인천국제공항(RKSI)",
  "temperature": 25.0,
  "windSpeed": 17,
  "visibility": "10SM",
  "weather": "맑음"
}
```

---

## 🚀 **5분만에 시작하기**

### **1단계: 준비물 체크** ✅
```bash
# Java 11 이상 설치 확인
java -version

# Kafka 서버 실행 중인지 확인 (필수!)
# 다른 터미널에서 Kafka 서버가 실행되어 있어야 합니다
```

### **2단계: 프로젝트 다운로드** 📥
이미 프로젝트가 있다면 이 단계는 건너뛰세요!

### **3단계: 설정 확인** ⚙️
`src/main/resources/application.properties` 파일을 열어서 Kafka 서버 주소만 확인하세요:

```properties
# 여러분의 Kafka 서버 주소로 변경하세요
spring.kafka.bootstrap-servers=13.209.157.53:9092,15.164.111.153:9092,3.34.32.69:9092
```

### **4단계: 실행하기** 🎉
```bash
# 1. 프로젝트 폴더로 이동
cd C:\Users\r2com\Desktop\kafka\aviation-weather-producer

# 2. 실행 (Windows)
.\gradlew.bat bootRun

# 또는 (Mac/Linux)
./gradlew bootRun
```

### **5단계: 성공 확인** 🎊
콘솔에 다음과 같은 메시지가 보이면 성공! 🎯

```
2025-05-30 16:45:35 [scheduling-1] INFO - METAR 데이터 수집 완료: 750 건
2025-05-30 16:45:37 [scheduling-1] INFO - METAR 데이터 카프카 전송 완료 - 성공: 750, 실패: 0
```

**축하합니다! 이제 자동으로 데이터가 수집되고 있어요!** 🎉

---

## 📋 **수집되는 데이터 상세 정보**

### 🏭 **METAR (공항 기상 데이터)**
**누가**: 전 세계 750개 주요 공항 기상 관측소  
**언제**: 1시간마다 자동 수집  
**무엇**: 실시간 공항 날씨 정보  

```json
{
  "icaoCode": "RKSI",
  "airportName": "인천국제공항",
  "temperature": 25.0,
  "dewpoint": 18.0,
  "windDirection": 250,
  "windSpeed": 17,
  "visibility": "10SM",
  "pressure": 1013.2,
  "weather": "FEW020 SCT100",
  "timestamp": "2025-05-30T16:00:00Z"
}
```

### 👨‍✈️ **PIREP (조종사 보고)**
**누가**: 실제 비행 중인 조종사들  
**언제**: 1시간마다 최신 1000건 수집  
**무엇**: 하늘에서 직접 겪은 날씨 상황  

```json
{
  "aircraftType": "B737",
  "altitude": 35000,
  "location": "RKSI 근처",
  "turbulence": "LIGHT",
  "icing": "NONE",
  "temperature": -45,
  "windSpeed": 120,
  "visibility": "UNLIMITED",
  "timestamp": "2025-05-30T15:30:00Z"
}
```

### 🚨 **SIGMET (기상 경보)**
**누가**: 각국 기상청 항공기상 전문가들  
**언제**: 1시간마다 현재 활성 경보 수집  
**무엇**: 항공기에 위험한 기상 현상 경보  

```json
{
  "hazardType": "TURBULENCE",
  "severity": "MODERATE",
  "area": "서울 FIR",
  "altitudeFrom": 25000,
  "altitudeTo": 45000,
  "validFrom": "2025-05-30T15:00:00Z",
  "validTo": "2025-05-30T21:00:00Z",
  "description": "제트기류로 인한 중간 강도 난기류"
}
```

---

## 🔧 **설정 변경하기**

### **수집 주기 변경**
`application.properties`에서 다음 부분을 수정:

```properties
# 현재: 1시간마다 (0 */60 * * * *)
aviation.weather.schedule.metar-cron=0 */60 * * * *
aviation.weather.schedule.pirep-cron=0 */60 * * * *
aviation.weather.schedule.sigmet-cron=0 */60 * * * *

# 30분마다로 변경하려면:
aviation.weather.schedule.metar-cron=0 */30 * * * *
aviation.weather.schedule.pirep-cron=0 */30 * * * *
aviation.weather.schedule.sigmet-cron=0 */30 * * * *

# 15분마다로 변경하려면:
aviation.weather.schedule.metar-cron=0 */15 * * * *
aviation.weather.schedule.pirep-cron=0 */15 * * * *
aviation.weather.schedule.sigmet-cron=0 */15 * * * *
```

### **Kafka 토픽명 변경**
```properties
# 기본 토픽명
aviation.weather.topics.metar=AWC-METAR
aviation.weather.topics.pirep=AWC-PIREP
aviation.weather.topics.sigmet=AWC-SIGMET

# 원하는 토픽명으로 변경
aviation.weather.topics.metar=my-weather-metar
aviation.weather.topics.pirep=my-weather-pirep
aviation.weather.topics.sigmet=my-weather-sigmet
```

### **Kafka 서버 주소 변경**
```properties
# 현재 설정된 서버들
spring.kafka.bootstrap-servers=13.209.157.53:9092,15.164.111.153:9092,3.34.32.69:9092

# 로컬 Kafka로 변경
spring.kafka.bootstrap-servers=localhost:9092

# 다른 서버로 변경
spring.kafka.bootstrap-servers=your-kafka-server:9092
```

---

## 📊 **프로젝트 구조 이해하기**

```
aviation-weather-producer/
│
├── 📁 src/main/java/com/aviation/weather/
│   ├── 🚀 AviationWeatherProducerApplication.java  # 시작점
│   │
│   ├── 📁 config/
│   │   ├── ⚙️ AviationWeatherProperties.java       # 설정 관리
│   │   └── 📡 KafkaConfig.java                     # Kafka 연결 설정
│   │
│   ├── 📁 model/
│   │   ├── 📊 MetarData.java                       # METAR 데이터 구조
│   │   ├── 📊 PirepData.java                       # PIREP 데이터 구조
│   │   └── 📊 HazardWeatherData.java               # SIGMET 데이터 구조
│   │
│   └── 📁 service/
│       ├── 🌐 AviationWeatherService.java          # API에서 데이터 수집
│       ├── 📤 AviationWeatherKafkaProducerService.java # Kafka로 데이터 전송
│       └── ⏰ AviationWeatherSchedulerService.java # 자동 스케줄링
│
├── 📁 src/main/resources/
│   └── 🔧 application.properties                   # 모든 설정 파일
│
├── 📁 logs/
│   └── 📝 aviation-weather-producer.log            # 실행 로그
│
└── 📋 README.md                                    # 이 문서
```

---

## 🔍 **실행 상태 확인하기**

### **콘솔 로그 보기**
정상 실행 시 다음과 같은 로그가 나타납니다:

```bash
# 애플리케이션 시작
2025-05-30 16:45:30 [main] INFO - Aviation Weather Producer 시작됨

# 초기 데이터 수집
2025-05-30 16:45:33 [scheduling-1] INFO - 초기 METAR 데이터 수집 시작
2025-05-30 16:45:35 [scheduling-1] INFO - METAR 데이터 수집 완료: 750 건
2025-05-30 16:45:37 [scheduling-1] INFO - METAR 데이터 카프카 전송 완료 - 성공: 750, 실패: 0

# 정기 스케줄 실행
2025-05-30 17:00:00 [scheduling-1] INFO - === METAR 데이터 정기 수집 시작 (2회차) ===
2025-05-30 17:00:02 [scheduling-1] INFO - METAR 데이터 수집 완료: 750 건
2025-05-30 17:00:04 [scheduling-1] INFO - METAR 데이터 카프카 전송 완료 - 성공: 750, 실패: 0
```

### **로그 파일 확인**
```bash
# 로그 파일 위치
tail -f logs/aviation-weather-producer.log

# Windows에서
type logs\aviation-weather-producer.log
```

### **Kafka 토픽 확인**
```bash
# Kafka 토픽 목록 보기
kafka-topics.sh --list --bootstrap-server localhost:9092

# 토픽 데이터 확인
kafka-console-consumer.sh --topic AWC-METAR --bootstrap-server localhost:9092
```

---

## 🚨 **문제 해결 가이드**

### **❌ "Connection refused" 오류**
**원인**: Kafka 서버가 실행되지 않음  
**해결책**:
1. Kafka 서버 먼저 실행
2. `application.properties`에서 Kafka 서버 주소 확인

### **❌ "API 호출 실패" 오류**
**원인**: 인터넷 연결 문제 또는 API 서버 문제  
**해결책**:
1. 인터넷 연결 확인
2. 방화벽 설정 확인
3. 잠시 후 재시도 (API 서버 일시적 문제일 수 있음)

### **❌ "토픽이 생성되지 않음"**
**원인**: Kafka 토픽 자동 생성 설정 문제  
**해결책**:
```bash
# 토픽 수동 생성
kafka-topics.sh --create --topic AWC-METAR --bootstrap-server localhost:9092
kafka-topics.sh --create --topic AWC-PIREP --bootstrap-server localhost:9092
kafka-topics.sh --create --topic AWC-SIGMET --bootstrap-server localhost:9092
```

### **❌ Java 버전 오류**
**원인**: Java 11 미만 버전 사용  
**해결책**: Java 11 이상 설치 및 환경변수 설정

---

## 📈 **모니터링 및 운영**

### **헬스체크 엔드포인트**
애플리케이션 실행 후 브라우저에서 확인:
- http://localhost:8080/actuator/health - 애플리케이션 상태
- http://localhost:8080/actuator/metrics - 성능 지표
- http://localhost:8080/actuator/info - 애플리케이션 정보

### **성능 모니터링**
```bash
# CPU 및 메모리 사용량 확인
top -p $(pgrep -f aviation-weather)

# 로그 실시간 모니터링
tail -f logs/aviation-weather-producer.log | grep -E "(완료|실패|ERROR)"
```

---

## 🎉 **성공적인 실행 확인 체크리스트**

- [ ] Kafka 서버가 실행 중입니다
- [ ] 애플리케이션이 오류 없이 시작되었습니다
- [ ] 콘솔에 "데이터 수집 완료" 메시지가 표시됩니다
- [ ] Kafka 토픽에 데이터가 전송되었습니다
- [ ] 1시간마다 자동으로 데이터가 수집됩니다

**모든 항목이 체크되었다면 축하합니다! 🎊**

---

## 💡 **추가 정보**

### **데이터 활용 아이디어**
- **실시간 날씨 대시보드** 구축
- **항공 지연 예측 모델** 개발
- **기상 알림 시스템** 구축
- **빅데이터 분석**을 통한 기상 패턴 연구

### **확장 가능성**
- 다른 기상 데이터 소스 추가
- 실시간 알림 시스템 연동
- 클라우드 배포 (AWS, GCP, Azure)
- 마이크로서비스 아키텍처로 분리

### **관련 기술 스택**
- **Spring Boot**: Java 웹 애플리케이션 프레임워크
- **Apache Kafka**: 대용량 실시간 데이터 스트리밍
- **WebFlux**: 비동기 HTTP 클라이언트
- **Jackson**: JSON 데이터 처리
- **Gradle**: 빌드 도구

---

## 🤝 **도움이 필요하다면**

문제가 발생하거나 추가 기능이 필요하다면:
1. 로그 파일 확인: `logs/aviation-weather-producer.log`
2. 설정 파일 재검토: `application.properties`
3. Kafka 서버 상태 확인
4. 인터넷 연결 및 방화벽 설정 확인

**이 README로도 해결되지 않는다면, 더 자세한 도움을 요청하세요!** 💪

---

## 🏆 **마무리**

**축하합니다!** 이제 여러분은:
- ✅ 전 세계 항공 기상 데이터를 실시간으로 수집하고
- ✅ Kafka를 통해 안정적으로 데이터를 전송하며
- ✅ 24시간 자동으로 운영되는 시스템을 구축했습니다!

**이 시스템으로 할 수 있는 것들:**
- 🌤️ 실시간 날씨 모니터링
- ✈️ 항공 운항 분석
- 📊 빅데이터 기반 기상 연구
- 🚨 위험 기상 조기 경보

**여러분의 데이터 파이프라인이 지금 이 순간에도 전 세계 하늘의 정보를 수집하고 있습니다!** 🌍✈️🎉