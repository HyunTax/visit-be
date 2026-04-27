# Sweet Home - Visit Reservation Backend

## Overview
방문 예약 관리 시스템 백엔드. 방문자 예약 CRUD와 토큰 기반 인증을 제공한다.

## Tech Stack
- **Java 25** / **Spring Boot 4.0.3** / **Maven**
- **MySQL 8** (JPA/Hibernate 7), **Redis** (Lettuce)
- **Lombok**, **spring-security-crypto** (BCrypt), **Jakarta Validation**
- **JavaMailSender** (HTML 메일 발송)

## Project Structure
```
src/main/java/com/sht4873/reservation/
├── core/
│   ├── annotation/                # @RequireAuth, @RequireAdmin
│   ├── component/                 # MailComponent (메일 발송), MailEventListener (이벤트 리스너)
│   ├── config/                    # Jpa(@EnableAsync), Redis, Security, Web 설정
│   ├── enums/                     # MailType (REQUEST / CHANGE / CANCEL)
│   ├── event/                     # MailEvent record
│   ├── exception/                 # VisitException (커스텀 예외)
│   ├── handler/                   # @RestControllerAdvice 전역 예외 처리
│   ├── interceptor/               # AuthInterceptor (토큰 검증)
│   └── util/                      # SecurityUtils (BCrypt + AES)
├── domain/
│   ├── auth/
│   │   ├── AuthController.java
│   │   ├── AuthService.java
│   │   ├── AuthRepository.java    # Redis 기반 (Spring Data X)
│   │   └── dto/request/
│   │       ├── AuthRequest.java         # name, phoneNum, password
│   │       └── AdminAuthRequest.java    # password
│   └── visitor/
│       ├── Visit.java             # JPA Entity (테이블: VISITOR)
│       ├── VisitController.java
│       ├── VisitService.java
│       ├── VisitRepository.java
│       └── dto/
│           ├── request/
│           │   ├── ReservationRequest.java       # name, phoneNum, visitDate, visitorCount, password, hasAllergy, memo
│           │   ├── ReservationSearchRequest.java # name, phoneNum, password
│           │   └── ReservationStatusRequest.java # statusMemo
│           └── response/
│               └── ReservationResponse.java      # password 제외, phoneNum 복호화
└── ReservationApplication.java
```

## Entity: Visit (테이블: VISITOR)
| 필드 | 컬럼 | 타입 | 설명 |
|------|------|------|------|
| id | ID | Long | PK, auto increment |
| name | NAME | String | 방문자 이름 |
| phoneNum | PHONE_NUM | String | AES-256 암호화 저장 |
| visitDate | VISIT_DATE | LocalDate | 방문일 |
| visitorCount | VISIT_COUNT | Long | 방문 인원 수 |
| password | PASSWORD | String | BCrypt 인코딩 저장 |
| hasAllergy | ALLERGY_YN | Boolean | 알러지 유무 |
| memo | MEMO | String | 메모 (nullable) |
| status | STATUS | Enum | WAIT / CONFIRM / REJECT / CANCEL |
| statusMemo | STATUS_MEMO | String | 상태 메모, REJECT/CANCEL 시 사용 |

## API Endpoints

### 인증 (`/v1/visit/auth`)
| Method | Path | Auth | 설명 |
|--------|------|------|------|
| POST | `/v1/visit/auth` | - | 일반 유저 토큰 발급 |
| POST | `/v1/visit/auth/admin` | - | 관리자 토큰 발급 (password만 전달) |

### 예약 (`/v1/visit/reservation`)
| Method | Path | Auth | 설명 |
|--------|------|------|------|
| POST | `/` | - | 예약 생성 |
| GET | `/find` | @RequireAuth | 내 예약 조회 |
| GET | `/all` | @RequireAdmin | 전체 예약 조회 |
| PUT | `/{id}` | @RequireAuth | 예약 수정 (visitDate, visitorCount, memo만 변경 가능) |
| DELETE | `/{id}` | @RequireAuth | 예약 취소 (status → CANCEL) |
| POST | `/{id}/confirm` | @RequireAdmin | 예약 승인 (status → CONFIRM) |
| POST | `/{id}/reject` | @RequireAdmin | 예약 거절 (status → REJECT, statusMemo 저장) |

## Mail System

예약 생성/수정/취소 시 관리자에게 HTML 메일 자동 발송.

### 흐름
```
VisitService → publishEvent(MailEvent)
    → [트랜잭션 커밋]
        → MailEventListener.handle() (@Async + @TransactionalEventListener(AFTER_COMMIT))
            → MailComponent.sendAdminMail()
                → admin.mail 목록 각각에 HTML 메일 발송
```

### MailType
| 타입 | 발송 시점 |
|------|-----------|
| REQUEST | 예약 생성 |
| CHANGE | 예약 수정 |
| CANCEL | 예약 취소 |

- 메일 실패 시 트랜잭션 롤백 없이 에러 로그만 기록 (`log.error` + 스택트레이스)
- CANCEL 타입은 메모 영역에 `statusMemo` (취소 사유) 표시, 나머지는 `memo` 표시

## Authentication

### 일반 유저
- `POST /v1/visit/auth` : name + phoneNum + password → VISITOR 테이블 조회 후 BCrypt 검증 → 토큰 발급
- Redis key: `RESERVATION:AUTH:{token}` → value: `{name}:{encryptedPhone}` (TTL 10분)
- `@RequireAuth` 검증: `RESERVATION:AUTH:` prefix 토큰 존재 여부 확인

### 관리자
- `POST /v1/visit/auth/admin` : password만 전달 → `admin.phone`(yaml)으로 VISITOR 테이블 조회 → BCrypt 검증 → 토큰 발급
- Redis key: `ADMIN:AUTH:{token}` → value: `{name}:{encryptedPhone}` (TTL 10분)
- `@RequireAdmin` 검증: `ADMIN:AUTH:` prefix 토큰 존재 여부 확인
- 관리자는 VISITOR 테이블에 일반 row로 저장, `admin.phone`으로 식별 (단일 관리자)

### 공통
- `Authorization: Bearer {token}` 헤더로 전달
- 전화번호: AES-256/ECB 암호화 후 저장, 응답 시 복호화

## Conventions

### Naming
- Controller/Service/Repository: `{Feature}Controller`, `{Feature}Service`
- DTO: `{Feature}{Type}Request/Response`
- Config: `{Feature}Configuration`
- DB 컬럼: UPPER_SNAKE_CASE, Java 필드: camelCase

### Patterns
- **DI**: 생성자 주입 우선
- **트랜잭션**: 쓰기 `@Transactional`, 읽기 `@Transactional(readOnly = true)`
- **DTO 변환**: `BeanUtils.copyProperties()` 사용, `Visit.convertEntity(request, ignoreProperties...)`
- **비밀번호**: BCrypt 인코딩, 응답 DTO에서 제외
- **전화번호**: AES-256 암호화 저장, `SecurityUtils.encryptPhone` / `decryptPhone`
- **예외**: `VisitException(message, HttpStatus)` → `BaseExceptionHandler`에서 처리
- **Validation**: Entity에 Jakarta Validation 어노테이션 (@NotEmpty, @NotNull)
- **비동기 메일**: `ApplicationEventPublisher.publishEvent(MailEvent)` → `@TransactionalEventListener(AFTER_COMMIT)` + `@Async` → 트랜잭션 커밋 후 별도 스레드 발송

### Error Response Format
```json
{ "message": "에러 메시지" }
```

## Configuration
- `application.yaml`: 기본 (port 10100)
- `application-local.yaml`: 로컬 개발 (port 8080)
- JPA: `ddl-auto: none` (수동 스키마 관리), `show-sql: true`
- CORS: `localhost:*` 허용, GET/POST/PUT/PATCH/DELETE
- `admin.phone`: 관리자 전화번호 (VISITOR 테이블 관리자 row 식별용)
- `admin.mail`: 관리자 메일 주소 목록 (`List<String>`, 다중 수신 가능)
- `admin.page-url`: 관리자 페이지 URL (메일 템플릿 내 링크)
- `security.aes-key`: 전화번호 AES-256 암호화 키 (32자, 운영 환경에서 반드시 변경)
- `spring.mail.*`: JavaMailSender SMTP 설정

## Build & Run
```bash
./mvnw spring-boot:run
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```
