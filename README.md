[English](#english) | [简体中文](#简体中文) | [한국어](#한국어)

# VibeCheck Finance

## English

### Introduction
**VibeCheck Finance** is not just another boring accounting tool. It’s a high-saturation, emotionally-driven financial diary that explores the correlation between your wallet and your mental state. We track **"Return on Happiness"** instead of just ROI.

Built with **Jetpack Compose** and designed with a bold **Neo-Brutalism** aesthetic, VibeCheck Finance makes managing money feel slightly less painful and significantly more expressive.

### Key Features
-   **Neo-Brutalism UI**: High-contrast colors (NeoYellow, NeoPink, NeoGreen), thick black borders, and hard shadows for a distinct, modern look.
-   **Emotion-Driven Tracking**: Record your mood with every transaction using our custom Mood Picker.
-   **Dashboard**: Get a quick glance at your "Net Happiness" vs "Net Spending" for the day.
-   **Smart Analytics**:
    -   **Happiness ROI**: A scatter plot correlating spending with happiness.
    -   **Stupidity Tax**: Visualize how much money you wasted on bad mood impulses.
    -   **Spending Trends**: Track your daily expenses over the last 30 days.
-   **Privacy First**: All data is stored locally on your device in a simple text format (`accounting_records.txt`). No cloud, no tracking.

### Tech Stack
-   **Language**: Kotlin
-   **UI Framework**: Jetpack Compose
-   **Architecture**: MVVM (Model-View-ViewModel)
-   **Navigation**: AndroidX Navigation Compose
-   **Charts**: MPAndroidChart
-   **Data Storage**: Local File System

### File Structure
```
app/src/main/java/ASIA/TPD/vibecheck/
├── data/
│   ├── FileRepository.kt       # Handles reading/writing to local file
│   ├── Transaction.kt          # Data model for transactions
│   └── TransactionViewModel.kt # State management for UI
├── ui/
│   ├── components/             # Reusable UI components (Neo-Brutalism style)
│   │   ├── MoodPicker.kt
│   │   ├── VibeButton.kt
│   │   ├── VibeCard.kt
│   │   └── VibeInput.kt
│   ├── screens/                # Application screens
│   │   ├── AddEntryScreen.kt
│   │   ├── AnalyticsScreen.kt
│   │   ├── DashboardScreen.kt
│   │   └── SettingsScreen.kt
│   └── theme/                  # Theme definitions (Colors, Type)
└── MainActivity.kt             # App entry point and Navigation host
```

### Quick Start
1.  Clone the repository.
2.  Open the project in **Android Studio**.
3.  Sync Gradle project.
4.  Run the application on an emulator or physical device (Minimum SDK 24).

### License
This software is licensed under the **MIT License**. See the [LICENSE](./LICENSE) file for details.

---

## 简体中文

### 项目简介
**VibeCheck Finance** 不仅仅是一个枯燥的记账工具。它是一个高饱和度、情感驱动的财务日记，探索你的钱包与心理状态之间的关联。我们要追踪的是 **“快乐回报率” (Return on Happiness)**，而不仅仅是投资回报率 (ROI)。

本项目采用 **Jetpack Compose** 构建，设计风格为大胆的 **新粗野主义 (Neo-Brutalism)**，让记账变得稍微不那么痛苦，并且更加充满个性。

### 核心功能
-   **新粗野主义 UI**: 高对比度配色（霓虹黄、粉、绿）、粗黑边框和硬阴影，打造独特的现代视觉体验。
-   **情感驱动记账**: 使用自定义心情选择器记录每一笔交易时的心情。
-   **仪表盘**: 快速查看当天的“净快乐值”与“净支出”。
-   **智能分析**:
    -   **快乐 ROI**: 关联支出与快乐值的散点图。
    -   **愚蠢税 (Stupidity Tax)**: 直观展示你在心情不好时冲动消费浪费了多少钱。
    -   **支出趋势**: 追踪过去 30 天的每日支出。
-   **隐私优先**: 所有数据以简单的文本格式 (`accounting_records.txt`) 本地存储在你的设备上。无云端同步，无追踪。

### 技术栈
-   **语言**: Kotlin
-   **UI 框架**: Jetpack Compose
-   **架构**: MVVM (Model-View-ViewModel)
-   **导航**: AndroidX Navigation Compose
-   **图表库**: MPAndroidChart
-   **数据存储**: 本地文件系统

### 目录结构
```
app/src/main/java/ASIA/TPD/vibecheck/
├── data/
│   ├── FileRepository.kt       # 处理本地文件的读写
│   ├── Transaction.kt          # 交易数据模型
│   └── TransactionViewModel.kt # UI 状态管理
├── ui/
│   ├── components/             # 可复用的 UI 组件 (新粗野主义风格)
│   │   ├── MoodPicker.kt
│   │   ├── VibeButton.kt
│   │   ├── VibeCard.kt
│   │   └── VibeInput.kt
│   ├── screens/                # 应用页面
│   │   ├── AddEntryScreen.kt
│   │   ├── AnalyticsScreen.kt
│   │   ├── DashboardScreen.kt
│   │   └── SettingsScreen.kt
│   └── theme/                  # 主题定义 (颜色, 排版)
└── MainActivity.kt             # 应用入口及导航主机
```

### 快速开始
1.  克隆仓库。
2.  在 **Android Studio** 中打开项目。
3.  同步 Gradle 项目。
4.  在模拟器或真机上运行应用 (最低支持 SDK 24)。

### 许可证
本项目基于 **MIT 许可证** 发布。详情见 [LICENSE](./LICENSE) 文件。

---

## 한국어

### 소개
**VibeCheck Finance**는 단순한 회계 도구가 아닙니다. 지갑과 정신 상태의 상관관계를 탐구하는 고채도, 감성 중심의 재정 일기입니다. 우리는 단순한 ROI 대신 **"행복 수익률 (Return on Happiness)"**을 추적합니다.

**Jetpack Compose**로 구축되고 대담한 **네오 브루탈리즘 (Neo-Brutalism)** 미학으로 디자인된 VibeCheck Finance는 돈 관리를 조금 덜 고통스럽고 훨씬 더 표현력 있게 만들어줍니다.

### 주요 기능
-   **네오 브루탈리즘 UI**: 고대비 색상(네오 옐로우, 핑크, 그린), 두꺼운 검은색 테두리, 딱딱한 그림자로 독특하고 현대적인 룩을 제공합니다.
-   **감성 중심 추적**: 커스텀 기분 선택기를 사용하여 모든 거래와 함께 기분을 기록하세요.
-   **대시보드**: 오늘의 "순 행복" 대 "순 지출"을 한눈에 확인하세요.
-   **스마트 분석**:
    -   **행복 ROI**: 지출과 행복의 상관관계를 보여주는 산점도.
    -   **멍청 비용 (Stupidity Tax)**: 기분이 나쁠 때 충동적으로 낭비한 돈이 얼마인지 시각화합니다.
    -   **지출 추세**: 지난 30일 동안의 일일 지출을 추적합니다.
-   **개인정보 보호 우선**: 모든 데이터는 장치에 간단한 텍스트 형식(`accounting_records.txt`)으로 로컬에 저장됩니다. 클라우드도, 추적도 없습니다.

### 기술 스택
-   **언어**: Kotlin
-   **UI 프레임워크**: Jetpack Compose
-   **아키텍처**: MVVM (Model-View-ViewModel)
-   **내비게이션**: AndroidX Navigation Compose
-   **차트**: MPAndroidChart
-   **데이터 저장소**: 로컬 파일 시스템

### 파일 구조
```
app/src/main/java/ASIA/TPD/vibecheck/
├── data/
│   ├── FileRepository.kt       # 로컬 파일 읽기/쓰기 처리
│   ├── Transaction.kt          # 거래 데이터 모델
│   └── TransactionViewModel.kt # UI 상태 관리
├── ui/
│   ├── components/             # 재사용 가능한 UI 컴포넌트 (네오 브루탈리즘 스타일)
│   │   ├── MoodPicker.kt
│   │   ├── VibeButton.kt
│   │   ├── VibeCard.kt
│   │   └── VibeInput.kt
│   ├── screens/                # 애플리케이션 화면
│   │   ├── AddEntryScreen.kt
│   │   ├── AnalyticsScreen.kt
│   │   ├── DashboardScreen.kt
│   │   └── SettingsScreen.kt
│   └── theme/                  # 테마 정의 (색상, 타이포그래피)
└── MainActivity.kt             # 앱 진입점 및 내비게이션 호스트
```

### 빠른 시작
1.  저장소를 복제합니다.
2.  **Android Studio**에서 프로젝트를 엽니다.
3.  Gradle 프로젝트를 동기화합니다.
4.  에뮬레이터 또는 실제 장치에서 애플리케이션을 실행합니다 (최소 SDK 24).

### 라이선스
이 소프트웨어는 **MIT 라이선스**로 배포됩니다. 자세한 내용은 [LICENSE](./LICENSE) 파일을 참조하세요.
