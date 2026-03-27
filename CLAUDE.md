# Sweet Home - Visit Reservation Backend

## Overview
방문 예약 관리 시스템 백엔드. 방문자 예약 CRUD와 토큰 기반 인증을 제공한다.

## Tech Stack
- **Java 25** / **Spring Boot 4.0.3** / **Maven**
- **MySQL 8** (JPA/Hibernate 7), **Redis** (Lettuce)
- **Lombok**, **spring-security-crypto** (BCrypt), **Jakarta Validation**

## Project Structure
```
src/main/java/com/sht4873/reservation/
├── core/                          # 공통 인프라
│   ├── annotation/                # @RequireAuth, @RequireAdmin
│   ├── config/                    # Jpa, Redis, Security, Web 설정
│   ├── exception/                 # VisitException (커스텀 예외)
│   ├── handler/                   # @RestControllerAdvice 전역 예외 처리
│   ├── interceptor/               # AuthInterceptor (토큰 검증)
│   └── util/                      # SecurityUtils (BCrypt)
├── domain/
│   ├── auth/                      # 인증 (토큰 발급/검증, Redis 저장)
│   │   ├── AuthController.java
│   │   ├── AuthService.java
│   │   ├── AuthRepository.java    # Redis 기반 (Spring Data X)
│   │   └── dto/request/
│   └── visitor/                   # 예약 관리 (CRUD)
│       ├── Visit.java             # JPA Entity (테이블: VISITOR)
│       ├── VisitController.java
│       ├── VisitService.java
│       ├── VisitRepository.java   # Spring Data JPA
│       └── dto/
└── ReservationApplication.java
```

## Architecture
- **Layered Architecture**: Controller → Service → Repository
- **도메인별 패키지 분리**: `auth`, `visitor`
- **core 패키지**: 도메인 횡단 관심사 (설정, 인터셉터, 예외, 유틸)

## API Endpoints

### 인증 (`/v1/visit/auth`)
| Method | Path | Auth | 설명 |
|--------|------|------|------|
| POST | `/v1/visit/auth` | - | 토큰 발급 |

### 예약 (`/v1/visit/reservation`)
| Method | Path | Auth | 설명 |
|--------|------|------|------|
| POST | `/` | - | 예약 생성 |
| GET | `/find` | @RequireAuth | 내 예약 조회 |
| GET | `/all` | @RequireAdmin | 전체 예약 조회 |
| PUT | `/{id}` | @RequireAuth | 예약 수정 (이름/전화번호/비밀번호 변경 불가) |
| DELETE | `/{id}` | @RequireAuth | 예약 취소 |

## Authentication
- UUID 토큰을 Redis에 저장 (TTL 10분)
- Redis key: `RESERVATION:AUTH:{token}` → value: `{name}:{phoneNum}`
- `Authorization: Bearer {token}` 헤더로 전달
- `AuthInterceptor`가 `@RequireAuth`, `@RequireAdmin` 어노테이션 기반으로 검증
- Admin 판별: 토큰의 전화번호와 `admin.phone` 프로퍼티 비교

## Conventions

### Naming
- Controller/Service/Repository: `{Feature}Controller`, `{Feature}Service`
- DTO: `{Feature}{Type}Request/Response`
- Config: `{Feature}Configuration`
- DB 컬럼: UPPER_SNAKE_CASE, Java 필드: camelCase

### Patterns
- **DI**: 생성자 주입 우선
- **트랜잭션**: 쓰기 `@Transactional`, 읽기 `@Transactional(readOnly = true)`
- **DTO 변환**: `BeanUtils.copyProperties()` 사용
- **비밀번호**: BCrypt 인코딩, 응답 DTO에서 제외
- **예외**: `VisitException(message, HttpStatus)` → `BaseExceptionHandler`에서 처리
- **Validation**: Entity에 Jakarta Validation 어노테이션 (@NotEmpty, @NotNull)

### Error Response Format
```json
{ "message": "에러 메시지" }
```

## Configuration
- `application.yaml`: 기본 (port 10100)
- `application-local.yaml`: 로컬 개발 (port 8080)
- JPA: `ddl-auto: none` (수동 스키마 관리), `show-sql: true`
- CORS: `localhost:*` 허용, GET/POST/PUT/PATCH/DELETE

## Build & Run
```bash
./mvnw spring-boot:run
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```
