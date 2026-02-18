[English](#english) | [简体中文](#简体中文) | [한국어](#한국어)

# VibeCheck Finance

[![Latest Release](https://img.shields.io/github/v/release/syweb-dev/VIBECHECK-Android?label=Latest%20Release)](https://github.com/syweb-dev/VIBECHECK-Android/releases/latest)

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
.
├── app/                                 # Android app module
│   ├── build.gradle.kts                 # Module Gradle config
│   └── src/                             # Module sources
│       ├── main/                        # Main source set
│       │   ├── AndroidManifest.xml      # Manifest and components
│       │   ├── java/ASIA/TPD/vibecheck/ # Kotlin sources (package)
│       │   │   ├── MainActivity.kt      # Entry point & NavHost
│       │   │   ├── data/                # Data layer
│       │   │   │   ├── FileRepository.kt       # Local file I/O
│       │   │   │   ├── LocaleManager.kt        # Locale persistence
│       │   │   │   ├── Transaction.kt          # Models & enums
│       │   │   │   └── TransactionViewModel.kt # State + persistence
│       │   │   └── ui/                  # UI layer
│       │   │       ├── components/      # Reusable Compose widgets
│       │   │       │   ├── MoodPicker.kt       # Emoji picker
│       │   │       │   ├── VibeButton.kt       # Styled button
│       │   │       │   ├── VibeCard.kt         # Shadowed card
│       │   │       │   └── VibeInput.kt        # Text input
│       │   │       ├── screens/         # App screens
│       │   │       │   ├── AddEntryScreen.kt   # Add transaction
│       │   │       │   ├── AnalyticsScreen.kt  # Charts & insights
│       │   │       │   ├── DashboardScreen.kt  # Summary & list
│       │   │       │   └── SettingsScreen.kt   # Locale & reset
│       │   │       └── theme/           # Theme setup
│       │   │           ├── Color.kt            # Colors
│       │   │           ├── Theme.kt            # Material3 theme
│       │   │           └── Type.kt             # Typography
│       │   └── res/                        # Resources
│       │       ├── values/                 # Default resources
│       │       │   ├── strings.xml         # Strings (en)
│       │       │   ├── colors.xml          # Color resources
│       │       │   ├── themes.xml          # Legacy theme bridge
│       │       │   └── arrays.xml          # Category arrays
│       │       ├── values-zh-rCN/          # Simplified Chinese
│       │       │   ├── strings.xml         # Strings (zh-CN)
│       │       │   └── arrays.xml          # Localized arrays
│       │       ├── mipmap-anydpi-v26/      # Adaptive icons
│       │       ├── mipmap-*/               # Density icons
│       │       ├── drawable/               # Drawables
│       │       │   ├── ic_launcher_background.xml # Icon bg
│       │       │   └── ic_launcher_foreground.xml # Icon fg
│       │       └── xml/                    # Config XMLs
│       │           ├── backup_rules.xml    # Auto backup rules
│       │           └── data_extraction_rules.xml # Backup redaction
│       ├── androidTest/                    # Instrumented tests
│       └── test/                           # Unit tests
├── build.gradle.kts                        # Root Gradle config
├── settings.gradle.kts                     # Gradle modules
├── gradle/                                 # Gradle wrapper
│   └── wrapper/                            # Wrapper files
├── gradle.properties                       # Build properties
├── LICENSE                                 # MIT license
├── README.md                               # Project docs
├── gradlew                                 # Unix wrapper
└── gradlew.bat                             # Windows wrapper
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
.
├── app/                                 # Android 应用模块
│   ├── build.gradle.kts                 # 模块 Gradle 配置
│   └── src/                             # 模块源码
│       ├── main/                        # 主源码集
│       │   ├── AndroidManifest.xml      # 应用清单与组件
│       │   ├── java/ASIA/TPD/vibecheck/ # Kotlin 源码包
│       │   │   ├── MainActivity.kt      # 入口与导航宿主
│       │   │   ├── data/                # 数据层
│       │   │   │   ├── FileRepository.kt       # 本地文件读写
│       │   │   │   ├── LocaleManager.kt        # 语言/区域设置持久化
│       │   │   │   ├── Transaction.kt          # 数据模型与枚举
│       │   │   │   └── TransactionViewModel.kt # 状态与持久化协调
│       │   │   └── ui/                  # 界面层
│       │   │       ├── components/      # 可复用 Compose 组件
│       │   │       │   ├── MoodPicker.kt       # 心情选择器
│       │   │       │   ├── VibeButton.kt       # 风格化按钮
│       │   │       │   ├── VibeCard.kt         # 阴影卡片
│       │   │       │   └── VibeInput.kt        # 文本输入
│       │   │       ├── screens/         # 页面
│       │   │       │   ├── AddEntryScreen.kt   # 新增记录
│       │   │       │   ├── AnalyticsScreen.kt  # 图表分析
│       │   │       │   ├── DashboardScreen.kt  # 汇总与列表
│       │   │       │   └── SettingsScreen.kt   # 语言与数据重置
│       │   │       └── theme/           # 主题配置
│       │   │           ├── Color.kt            # 色板
│       │   │           ├── Theme.kt            # Material3 主题封装
│       │   │           └── Type.kt             # 排版
│       │   └── res/                        # 资源目录
│       │       ├── values/                 # 默认资源
│       │       │   ├── strings.xml         # 英文字符串
│       │       │   ├── colors.xml          # 颜色资源
│       │       │   ├── themes.xml          # 主题桥接
│       │       │   └── arrays.xml          # 分类等数组
│       │       ├── values-zh-rCN/          # 简体中文资源
│       │       │   ├── strings.xml         # 中文字符串
│       │       │   └── arrays.xml          # 本地化数组
│       │       ├── mipmap-anydpi-v26/      # 自适应图标
│       │       ├── mipmap-*/               # 各密度图标
│       │       ├── drawable/               # 可绘制资源
│       │       │   ├── ic_launcher_background.xml # 图标背景
│       │       │   └── ic_launcher_foreground.xml # 图标前景
│       │       └── xml/                    # 其他 XML 配置
│       │           ├── backup_rules.xml    # 备份规则
│       │           └── data_extraction_rules.xml # 备份抽取规则
│       ├── androidTest/                    # 仪器化测试
│       └── test/                           # 单元测试
├── build.gradle.kts                        # 根 Gradle 配置
├── settings.gradle.kts                     # 模块设置
├── gradle/                                 # Gradle 包装器
│   └── wrapper/                            # 包装器文件
├── gradle.properties                       # 构建属性
├── LICENSE                                 # 许可证 (MIT)
├── README.md                               # 项目说明
├── gradlew                                 # Unix 包装脚本
└── gradlew.bat                             # Windows 包装脚本
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
.
├── app/                                 # Android 앱 모듈
│   ├── build.gradle.kts                 # 모듈 Gradle 설정
│   └── src/                             # 모듈 소스
│       ├── main/                        # 메인 소스셋
│       │   ├── AndroidManifest.xml      # 매니페스트 및 컴포넌트
│       │   ├── java/ASIA/TPD/vibecheck/ # Kotlin 소스 패키지
│       │   │   ├── MainActivity.kt      # 진입점 & 내비 호스트
│       │   │   ├── data/                # 데이터 레이어
│       │   │   │   ├── FileRepository.kt       # 로컬 파일 I/O
│       │   │   │   ├── LocaleManager.kt        # 로케일 유지
│       │   │   │   ├── Transaction.kt          # 모델/열거형
│       │   │   │   └── TransactionViewModel.kt # 상태/영속 중재
│       │   │   └── ui/                  # UI 레이어
│       │   │       ├── components/      # 재사용 Compose 컴포넌트
│       │   │       │   ├── MoodPicker.kt       # 이모지 선택
│       │   │       │   ├── VibeButton.kt       # 스타일 버튼
│       │   │       │   ├── VibeCard.kt         # 그림자 카드
│       │   │       │   └── VibeInput.kt        # 텍스트 입력
│       │   │       ├── screens/         # 화면
│       │   │       │   ├── AddEntryScreen.kt   # 거래 추가
│       │   │       │   ├── AnalyticsScreen.kt  # 차트/인사이트
│       │   │       │   ├── DashboardScreen.kt  # 요약/목록
│       │   │       │   └── SettingsScreen.kt   # 로케일/데이터 초기화
│       │   │       └── theme/           # 테마 설정
│       │   │           ├── Color.kt            # 색상 팔레트
│       │   │           ├── Theme.kt            # Material3 테마
│       │   │           └── Type.kt             # 타이포그래피
│       │   └── res/                        # 리소스
│       │       ├── values/                 # 기본 리소스
│       │       │   ├── strings.xml         # 영문 문자열
│       │       │   ├── colors.xml          # 색상 리소스
│       │       │   ├── themes.xml          # 테마 브리지
│       │       │   └── arrays.xml          # 카테고리 배열
│       │       ├── values-zh-rCN/          # 간체 중국어 리소스
│       │       │   ├── strings.xml         # 중국어 문자열
│       │       │   └── arrays.xml          # 현지화 배열
│       │       ├── mipmap-anydpi-v26/      # 적응형 아이콘
│       │       ├── mipmap-*/               # 밀도별 아이콘
│       │       ├── drawable/               # 드로어블
│       │       │   ├── ic_launcher_background.xml # 아이콘 배경
│       │       │   └── ic_launcher_foreground.xml # 아이콘 전경
│       │       └── xml/                    # 기타 XML 설정
│       │           ├── backup_rules.xml    # 백업 규칙
│       │           └── data_extraction_rules.xml # 백업 추출 규칙
│       ├── androidTest/                    # 계측 테스트
│       └── test/                           # 단위 테스트
├── build.gradle.kts                        # 루트 Gradle 설정
├── settings.gradle.kts                     # 모듈 설정
├── gradle/                                 # Gradle 래퍼
│   └── wrapper/                            # 래퍼 파일
├── gradle.properties                       # 빌드 속성
├── LICENSE                                 # 라이선스 (MIT)
├── README.md                               # 프로젝트 문서
├── gradlew                                 # 유닉스 래퍼
└── gradlew.bat                             # 윈도우 래퍼
```

### 빠른 시작
1.  저장소를 복제합니다.
2.  **Android Studio**에서 프로젝트를 엽니다.
3.  Gradle 프로젝트를 동기화합니다.
4.  에뮬레이터 또는 실제 장치에서 애플리케이션을 실행합니다 (최소 SDK 24).

### 라이선스
이 소프트웨어는 **MIT 라이선스**로 배포됩니다. 자세한 내용은 [LICENSE](./LICENSE) 파일을 참조하세요.
