# 📱 ToolWithMe Mobile (Android)

> JWT 기반 인증을 유지한 상태에서 Spring Boot REST API와 통신하며,  
> 공구 이미지 분석 기능을 네이티브 Android 환경으로 확장한 AI 서비스 모바일 클라이언트

---

## 📋 프로젝트 개요 (Project Overview)

ToolWithMe Mobile은 기존 Spring Boot 기반 웹 서버와 연동하여  
공구 이미지 분석 기능을 Android 네이티브 환경으로 확장한 클라이언트 애플리케이션입니다.

이미지 업로드 → 서버 분석 → 결과 수신 → 공구 설명 및 관련 영상 제공까지  
REST API 기반 통신 구조로 구현하였습니다.

| 항목 | 내용 |
|------|------|
| 플랫폼 | Android |
| 개발 환경 | Android Studio |
| 네트워크 통신 | Retrofit2 |
| 인증 방식 | JWT (Access / Refresh Token) |
| 배포 | Google Play Store 출시 완료 |

---

## ✨ 주요 기능 (Key Features)

### 🔐 1. JWT 기반 로그인 시스템
- ID / Password 로그인
- 서버에서 Access / Refresh Token 발급
- SharedPreferences에 토큰 저장
- OkHttp Interceptor를 통한 자동 Authorization 헤더 삽입
- 앱 재실행 시 자동 인증 유지

### 🧠 2. 이미지 분석 기능
- 갤러리 또는 카메라를 통한 이미지 선택
- Multipart 형식으로 서버 업로드
- Spring Boot → Django 추론 서버로 전달
- JSON 응답 수신 후:
  - 예측 공구명 표시
  - 공구 설명 출력
  - 관련 YouTube 영상 연동

### 🧰 3. 게시판 및 커뮤니티 연동
- 게시글 목록 조회 (RecyclerView)
- 게시글 상세 조회
- 댓글 CRUD 연동
- 토큰 기반 사용자 인증 유지

### 👤 4. 마이페이지 기능
- 회원 정보 조회
- 비밀번호 변경
- 계정 관리 기능

---

## 🏗 아키텍처 (Architecture)

```
Android App
     ▼
Spring Boot (REST API, JWT 인증)
     ▼
Django (ResNet50 추론 서버)
```

**Android**
- Retrofit2 기반 API 인터페이스 분리
- OkHttp Interceptor 기반 JWT 자동 삽입
- Multipart 이미지 업로드 처리

**Spring Boot**
- JWT 인증 및 사용자 관리
- 이미지 분석 요청 전달
- DB 조회 및 YouTube URL 매핑

**Django**
- PyTorch ResNet50 모델 로드
- 이미지 추론 수행
- JSON 응답 반환

---

## 🛠 기술 스택 (Tech Stack)

| Category | Technology |
|----------|------------|
| Language | Kotlin |
| Network | Retrofit2, OkHttp |
| Authentication | JWT |
| Image Upload | Multipart (Retrofit) |
| UI | XML Layout + Activity |
| Server | Spring Boot |
| AI Inference | Django REST Framework + PyTorch |
| Deployment | Google Play Store |

---

## 🚀 실행 방법 (Local Build)

### 1️⃣ 프로젝트 클론
```bash
git clone https://github.com/hansol418/AndroidStudioProject3.git
```

### 2️⃣ Android Studio 실행
- Android Studio에서 프로젝트 열기
- Gradle Sync 완료 후 실행

### 3️⃣ BASE_URL 설정 확인
`MyApplication` 또는 `RetrofitClient` 내부의 BASE_URL이 Spring Boot 서버 주소로 설정되어 있는지 확인

---

## 🔗 API 통신 구조

**로그인**
```
POST /api/login
→ Access / Refresh Token 발급
```

**이미지 분석**
```
POST /classify (Multipart)
```

**게시판 조회**
```
GET /api/board
```

---

## 📱 Google Play 출시 (Deployment)

ToolWithMe Mobile은 Google Play Store에 정식 출시되었습니다.

👉 [Play Store 링크](https://play.google.com/store/apps/details?id=com.sylovestp.firebasetest.testspringrestapp&pcampaignid=web_share)

---

## 👤 담당 역할 (My Contribution)

| 기능 | 기여도 |
|------|--------|
| Retrofit2 기반 REST API 네트워크 레이어 설계 | 100% |
| JWT 기반 로그인 및 토큰 자동 인증 구조 구현 | 100% |
| 이미지 Multipart 업로드 및 서버 응답 처리 구현 | 100% |
| 게시판 및 댓글 기능 Android 연동 | 100% |
| Google Play Store 배포 및 출시 과정 수행 | 100% |

