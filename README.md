# CommitLog

**"개발자의 어제를 기록하고, 내일의 성장을 데이터로 커밋하세요."**

CommitLog는 단순한 기록장을 넘어, **내장된 지능형 프롬프트 엔진**이

당신의 학습 텍스트를 심층 분석하여 정량화하는 **개발자 전용 성장 데이터 대시보드**입니다.

<br>

## ✨ 주요 기능 (Key Features)

### 🤖 내장형 프롬프트 분석 엔진 (Background AI)

> 사용자가 입력한 학습 내용을 바탕으로, 내장된 최적화 프롬프트가 기술적 맥락을 파악합니다.
> 
- **비동기 분석 처리**: `WorkManager`를 통해 기록 저장 즉시 백그라운드에서 분석이 시작됩니다. 사용자는 분석 완료를 기다릴 필요 없이 즉시 다음 활동이 가능합니다.
- **지능형 태그 추출**: 본문에서 언급된 기술 스택(Kotlin, Compose, Room 등)을 `LearningTag`로 자동 분류합니다.
- **학습 심도 측정**: 기록의 내용을 다각도로 분석하여 학습의 깊이를 `DifficultyLevel`로 수치화합니다.


### 📊 데이터 기반 성장 인사이트 (Insights)

> 꾸준함의 가치를 숫자가 아닌 시각적 데이터로 증명합니다.
> 
- **기술 밸런스 대시보드**: `Vico` 라이브러리를 활용해 기술 스택별 학습 비중을 직관적인 차트로 제공합니다.
- **생산성 통계**: 요일별 기록 빈도와 난이도 변화 추이를 분석하여 본인의 학습 패턴을 객관적으로 파악할 수 있습니다.
- **성장 궤적**: 시간이 지남에 따라 변하는 기술적 난이도의 흐름을 선형 그래프로 확인하세요.

### ✍️ 무중단 기록 시스템 (Draft & Persistence)

> 생각의 흐름이 끊기지 않도록 모든 순간을 기록합니다.
> 
- **실시간 드래프트 저장**: `Room DB` 기반의 실시간 임시 저장 시스템이 타이핑 중인 모든 내용을 추적하여 앱 종료 시에도 완벽하게 복구합니다.
- **강력한 통합 검색**: 제목, 본문 내용은 물론 분석 엔진이 생성한 자동 태그를 기반으로 과거의 기록을 신속하게 찾아낼 수 있습니다.

<br>

## 🛠 Tech Stack & Architecture

### 🏗 Architecture

본 프로젝트는 **Clean Architecture**와 **MVI(Model-View-Intent)** 패턴을 준수하여 설계되었습니다.

- **Modules**:
    - `:app`: Hilt 진입점 및 전역 초기화.
    - `:presentation`: Compose 기반 UI, MVI 패턴의 `UiState`/`Effect` 관리.
    - `:domain`: 핵심 비즈니스 로직(UseCase) 및 순수 Kotlin 엔티티.
    - `:data`: API 연동(Retrofit), 데이터 영속성(Room), 백그라운드 Worker 구현.

### 💻 Technologies

### 주요 기술 스택 (Tech Stack)

| 분류 | 상세 내역 |
| --- | --- |
| **Language** | Kotlin (Coroutines, Flow) |
| **UI Framework** | Jetpack Compose, Material Design 3, Glance (Widget) |
| **Dependency Injection** | Hilt |
| **Database** | Room Persistence |
| **Networking** | Retrofit 2, OkHttp 3 |
| **Analysis System** | Gemini 기반 텍스트 분석 자동화 |
| **Background Processing** | WorkManager |
| **Visualization** | Vico Chart Library |

<br>

## 📸 Screen Previews

*사용자 중심의 다크 모드 인터페이스와 일관된 디자인 시스템을 제공합니다.*

<table> 
  <tr> 
    <td align="center">
      <b>홈 화면</b>
    </td> 
    <td align="center">
      <b>기록 화면</b>
    </td> <td align="center">
      <b>상세 화면</b>
    </td> 
  </tr>
  <tr> 
    <td align="center"> 
      <img width="454" height="1012" alt="Home Screen" src="https://github.com/user-attachments/assets/9c034043-9adb-4cc8-a2cf-7681c502764d" />
    </td> 
    <td align="center">
      <img width="454" height="1012" alt="Settings Screen" src="https://github.com/user-attachments/assets/353b9f3d-9008-4696-90ce-296a0787ba31" /> 
    </td> 
    <td align="center">
      <img width="454" height="1012" alt="Profile Screen" src="https://github.com/user-attachments/assets/40d6e3af-f912-4d6e-a104-39f73c3d857c" /> 
    </td> 
  </tr> 
</table>

<table> 
  <tr> 
    <td align="center">
      <b>주간 통계</b>
    </td> 
    <td align="center">
      <b>월간 통계</b>
    </td>
    <td align="center">
      <b>연간 통계</b>
    </td>
    <td align="center">
      <b>월간 회고</b>
    </td>
  </tr>
  <tr> 
    <td align="center">
      <img width="454" height="1012" alt="Calendar Screen" src="https://github.com/user-attachments/assets/696fc3e0-50ad-4a35-ba4e-67ea23fb3f43" /> 
    </td>
    <td align="center"> 
      <img width="454" height="1012" alt="History Screen" src="https://github.com/user-attachments/assets/505a716d-09b0-4536-9dc2-a8d4812c58fa" />
    </td> 
    <td align="center"> 
      <img width="454" height="1012" alt="Analysis Screen" src="https://github.com/user-attachments/assets/c1b72afd-2820-42ec-9fd1-01a3a1320923" /> 
    </td>
    <td align="center"> 
      <img width="454" height="1012" alt="Group Screen" src="https://github.com/user-attachments/assets/b95ce051-6013-4afd-a64b-6f5b574327ed" /> 
    </td> 
  </tr> 
</table>

## ⚙️ Getting Started

1. **API Key 설정**`local.properties` 파일에 분석 엔진 구동을 위한 키를 추가합니다.
    
    ```
    GEMINI_API_KEY="your_api_key"
    ```
    
2. **빌드 및 실행**
Android Studio에서 프로젝트를 열고 `:app` 모듈을 실행하세요.
