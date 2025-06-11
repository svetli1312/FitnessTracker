# FitnessTrackerApp

A mobile app built in Android Studio using Kotlin and Jetpack Compose to help users track daily calorie intake, macronutrient goals, and BMI. The app allows users to log meals, view progress, and visualize goal achievement using a custom calendar view.

## Features

- Add and manage daily meals with calorie and macro values
- Calculate and update BMI and daily nutritional targets
- View daily progress with ✅ or ❌ indicators
- Calendar-based UI to track which days goals were met
- Store data locally using SQLite
- API integration for:
  - USDA FoodData Central nutrition data (via Retrofit)
  - Optional Azure IoT temperature data for heat-based fitness logic

## Technologies Used

- Kotlin
- Jetpack Compose
- SQLite (Room optional)
- Retrofit (for API communication)
- Android ViewModel / State Management
- Shared Preferences or Internal Storage

## Folder Structure
📁 app/
├── 📁 src/
│   ├── 📁 main/
│   │   ├── 📁 java/…          # Kotlin source files
│   │   ├── 📁 res/              # Layouts, drawables, themes
│   │   └── AndroidManifest.xml
├── build.gradle
└── README.md
## How to Run

1. Clone the repository:
   git clone https://github.com/svetli1312/FitnessTrackerApp.git
2. Open the project in **Android Studio**

3. Sync Gradle and install dependencies

4. Run on a real device or emulator

## Screenshots

(You can add screenshots later here to show UI, calendar view, add-meal screen, etc.)

## Security Note

If using APIs (e.g., USDA or Azure), make sure keys are stored in `local.properties` or environment variables, and are not committed to the repo.

## Status

Fully functional and tested locally. Designed for educational and demonstration purposes.
