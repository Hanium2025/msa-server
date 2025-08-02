# 🛒 MSA 기반 중고거래 서비스

## 📌 프로젝트 개요
MSA(Microservice Architecture)를 기반으로 설계된 중고거래 플랫폼입니다.  
각 서비스는 gRPC 기반 통신 및 Docker 환경에서 구성되어 있습니다.
현재 프로젝트는 개발 진행 중이며, 핵심 기능부터 점차 구현해나갈 예정입니다.



## 🧩 프로젝트 준비 과정

### 1. 📌 **이벤트 스토밍 및 서비스 분리**
- 전체 서비스에 대한 이벤트 스토밍 진행
- 이를 바탕으로 마이크로서비스 단위로 도메인 및 책임 분리
- **회고록 작성 및 팀 내 공유 완료**

### 2. 📃 **수행 계획 수립**
- **유저 스토리 기반 플래닝 포커** 진행
- **스크럼 방식**을 도입하여 주간 단위 진행
- **INVEST 원칙** 기반으로 유저 스토리 작성

### 3. 📚 **도메인 모델링 및 ERD**
- 도메인 모델 식별 및 도메인 간 연관 관계 설계
- ERD 설계 및 시각화 완료

| 전체 서비스 흐름 이벤트 스토밍 | 도메인 모델링 |  ERD |
|:------------------------------:|:---------------------:|:------------------------------------:|
| <img src="https://github.com/user-attachments/assets/59731b0c-881e-4647-8593-b7f46ed06804" width="250"/> | <img src="https://github.com/user-attachments/assets/b7dd908d-1b1e-46d8-bfaf-551bae4aca48" width="250"/> <br>  | <img src="https://github.com/user-attachments/assets/b041d071-f5ee-4afb-bfe4-69c041f42a9b" width="250"/> <br> |



### 4. 🔌 **스터디 세션**

| 주제 | 담당자 | 관련 문서 |
|------|--------|-------------|
| gRPC 통신 방식 | 허민영 | [바로가기 🔗](https://www.notion.so/gRPC-21bafe091f2380a78d59cecdb7c42e7f) |
| Docker & Compose 환경 구축 | 김민주 | [바로가기 🔗](https://www.notion.so/Docker-220afe091f2380ed923af01cbba98a74) |
| React Native (Expo) 환경 구성 | 남지연 | [바로가기 🔗](https://www.notion.so/Expo-21bafe091f23804f8ed8d65f0e277f94) |
| React Native + TypeScript를 활용한 홈화면 구성 (with Expo Router) | 선지오 | [바로가기 🔗](https://www.notion.so/2-21bafe091f23801ca0fbdb73f931d75b) |
| 프로젝트 초기 세팅 | 허민영 | [바로가기 🔗](https://www.notion.so/223afe091f2380c4a5def49adcd7f876) |
| AWS 기초 + CI/CD | 김가현 | [바로가기 🔗](https://maize-splash-6f6.notion.site/CICD-21fafe091f238069bf25e5a95c7645ec?source=copy_link) |

현재까지의 스터디 내용은 위 표에 정리되어 있으며,  
추가 세션이 진행되는 대로 내용을 계속 업데이트할 예정입니다.

### 5. 🗣️ **Daily Scrum**
[바로가기 🔗](https://maize-splash-6f6.notion.site/Daily-Scrum-223afe091f238008a47ade5675465b18?source=copy_link) 


### 6. 🏗️ Architecture
<img width="1229" height="718" alt="image" src="https://github.com/user-attachments/assets/dbf2b96e-d6cc-4010-916a-7a40c5e4e4bd" />

#### 📁 정적 리소스 처리
모든 정적 리소스(이미지 등)는 Amazon S3에 저장됩니다.

S3에 저장된 파일은 CloudFront를 통해 전 세계 엣지 서버에서 빠르게 제공됩니다.

#### 🔁 백엔드 요청 흐름
사용자 요청은 API Gateway를 통해 각 백엔드 서비스로 전달됩니다.

모든 백엔드 서비스는 AWS ECS 클러스터의 Private Subnet 환경에서 실행됩니다.

ECS는 ECR에 저장된 Docker 이미지를 자동으로 가져와 컨테이너로 구동합니다.

서비스 간 통신은 gRPC 프로토콜을 기반으로 이루어집니다.

#### 🔍 모니터링 및 트레이싱 시스템
Zipkin을 통해 서비스 간 호출 및 요청 흐름을 분산 추적(Distributed Tracing)합니다.

Prometheus가 메트릭 데이터를 수집하며, 이를 Grafana로 시각화하여 실시간으로 시스템 상태를 모니터링합니다.

#### 🛠️ 트러블 슈팅

- Config-Service와 Discovery-Service 간 순환 참조 문제
- gRPC 호출 시 Metadata 누락으로 인한 NullPointerException
https://www.notion.so/23660b1c7f7780118f0fd3f7180324aa

## ⚙️ 기술 스택

### Backend
- Java 21, Spring Boot 3.2.4 (Security, JPA, Validation)
- MapStruct, Lombok, Hibernate Types

### Database
- MySQL (운영)
- H2 (테스트)

### MSA & 통신
- gRPC + Protocol Buffers
- Spring Cloud 2023.0.0 (Eureka Client)
- grpc-spring-boot-starter

### 인증/보안
- JJWT (JWT 인증)

### 인프라
- Docker / Docker Compose
- Eureka Server

### 개발 및 테스트
- DevTools, JUnit (useJUnitPlatform)

