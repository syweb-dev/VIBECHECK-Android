# [VibeCheck Finance] Spec

## Why
Implement the "VibeCheck Finance" Android application as described in the PRD (`e:\CODE\VIBECODE_ANDROID\🛠️ Product Requirements Document (PRD)- VibeCheck Finance.md`). The goal is to create a financial diary that tracks "Return on Happiness" with a Neo-Brutalism design style.

## What Changes
- **New Dependencies**: Add `androidx-navigation-compose` for navigation and `com.github.PhilJay:MPAndroidChart` (or similar) for analytics charts.
- **Data Layer**: Implement a local file-based repository (`accounting_records.txt`) to store transaction data (Date, Type, Amount, Notes, Mood).
- **UI Layer**:
  - Implement a Neo-Brutalism theme (High saturation, dark outlines, bold typography).
  - Create reusable UI components (Buttons, Cards, Input Fields).
  - Implement screens: Dashboard (Home), Add Entry, Annual Analytics, Settings.
- **Navigation**: Set up a navigation graph to switch between screens.

## Impact
- **Affected specs**: None (new project).
- **Affected code**: All new files in `ASIA.TPD.vibecheck` package.

## ADDED Requirements
### Requirement: Data Persistence
The system SHALL store all transaction records in a local text file named `accounting_records.txt` in the app's private storage.
The format SHALL be pipe-separated values: `YYYY-MM-DD HH:MM:SS|Type|Amount|Notes|Mood`.

### Requirement: Neo-Brutalism UI
The UI SHALL follow the Neo-Brutalism design language:
- High contrast colors (e.g., Bright Yellow, Pink, Cyan).
- Thick black borders/outlines on UI elements.
- Bold typography.
- Simple, geometric shapes.

### Requirement: Dashboard
The Dashboard SHALL display:
- Daily feed of transactions.
- Net Happiness vs Net Spending summary for the current day.

### Requirement: Add Entry
The Add Entry screen SHALL allow users to:
- Select Transaction Type (Income/Expense).
- Enter Amount via a custom numeric keypad.
- Select Mood via emoji picker.
- Add optional notes.
- Save the entry to the local file.

### Requirement: Analytics
The Analytics screen SHALL display:
- "Happiness ROI" scatter plot (Mood vs Amount).
- "Stupidity Tax" pie chart (Expenses with low mood scores).
- Monthly trend lines.

## MODIFIED Requirements
None.

## REMOVED Requirements
None.
