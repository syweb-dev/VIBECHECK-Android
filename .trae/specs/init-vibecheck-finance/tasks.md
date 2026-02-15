# Tasks

- [x] Task 1: Project Setup
  - [x] SubTask 1.1: Add dependencies (Navigation, Charts, etc.)
  - [x] SubTask 1.2: Create theme resources (Colors, Typography, Shapes) following Neo-Brutalism style.
  - [x] SubTask 1.3: Create basic app structure (MainActivity, NavHost).

- [x] Task 2: Data Layer
  - [x] SubTask 2.1: Define `Transaction` data model.
  - [x] SubTask 2.2: Implement `FileRepository` to handle `accounting_records.txt` reading/writing.
  - [x] SubTask 2.3: Implement `TransactionViewModel` to expose data to UI.

- [x] Task 3: UI Components
  - [x] SubTask 3.1: Create `VibeButton` (Custom Button with Neo-Brutalism style).
  - [x] SubTask 3.2: Create `VibeCard` (Custom Card with Neo-Brutalism style).
  - [x] SubTask 3.3: Create `VibeInput` (Custom Input Field with Neo-Brutalism style).
  - [x] SubTask 3.4: Create `MoodPicker` (Emoji based).

- [x] Task 4: Add Entry Screen
  - [x] SubTask 4.1: Implement UI layout (Amount, Type, Mood, Notes).
  - [x] SubTask 4.2: Implement custom numeric keypad.
  - [x] SubTask 4.3: Connect ViewModel to save entry.

- [x] Task 5: Dashboard Screen
  - [x] SubTask 5.1: Implement UI layout (Header summary, Transaction list).
  - [x] SubTask 5.2: Connect ViewModel to display transactions.
  - [x] SubTask 5.3: Calculate and display daily summary (Net Happiness vs Net Spending).

- [x] Task 6: Analytics Screen
  - [x] SubTask 6.1: Implement UI layout (Charts).
  - [x] SubTask 6.2: Implement `Happiness ROI` scatter plot logic and view.
  - [x] SubTask 6.3: Implement `Stupidity Tax` pie chart logic and view.
  - [x] SubTask 6.4: Implement Monthly Trend lines logic and view.

- [x] Task 7: Settings Screen
  - [x] SubTask 7.1: Implement UI layout (Theme toggle, Data reset).
  - [x] SubTask 7.2: Implement Data Reset logic (clear `accounting_records.txt`).
  - [x] SubTask 7.3: Implement Theme toggle logic (Dark/Light/System).

- [x] Task 8: Navigation & Integration
  - [x] SubTask 8.1: Setup Navigation Graph (Home -> Add Entry, Home -> Analytics, Home -> Settings).
  - [x] SubTask 8.2: Ensure smooth transitions and data updates.

# Task Dependencies
- Task 2 depends on Task 1.
- Task 3 depends on Task 1.
- Task 4 depends on Task 2 and Task 3.
- Task 5 depends on Task 2 and Task 3.
- Task 6 depends on Task 2 and Task 3.
- Task 7 depends on Task 2 and Task 3.
- Task 8 depends on all previous tasks.
