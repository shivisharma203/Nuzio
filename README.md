# Nuzio 📰

Nuzio is a modern Android news application built using **Jetpack Compose**, **Firebase Authentication**, and following **Clean Architecture with MVVM**. It provides a clean, responsive UI to browse the latest news articles, supporting social login via Google and Facebook through Firebase.

---

## ✨ Features

- 🔥 Jetpack Compose UI
- 🔐 Firebase Authentication (Google & Facebook Sign-In)
- 📰 Dynamic news feed with smooth navigation
- ✅ Clean Architecture + MVVM pattern
- 🚀 Maintainable, scalable codebase

---


## 🧰 Tech Stack

- Kotlin
- Jetpack Compose
- Firebase Authentication
- MVVM
- Clean Architecture
- Retrofit (optional, for API calls)
- Coroutines + Flows (optional)
- Material 3 Design Components

---

## 🏗️ App Architecture

The app is organized using **Clean Architecture** principles with three main layers:

### 1. Presentation Layer
- Jetpack Compose UI
- ViewModels manage UI state and expose data streams
- UI only interacts with ViewModel, keeping it decoupled from business logic

### 2. Domain Layer
- Contains core business logic via UseCases
- Defines repository interfaces
- Pure Kotlin with no Android dependencies

### 3. Data Layer
- Implements repository interfaces
- Handles remote (Retrofit/Firebase) and local (Room/SharedPreferences) data sources
- Converts raw data into domain models

```text
+---------------------+
|    Presentation     | <- Compose + ViewModel
+---------------------+
          ↓
+---------------------+
|       Domain        | <- UseCases + Repository interfaces
+---------------------+
          ↓
+---------------------+
|        Data         | <- API clients, Firebase, DB, repository impl
+---------------------+


/nuzio
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/yourpackage/nuzio/
│   │   │   │   ├── di/                # Dependency Injection setup
│   │   │   │   ├── ui/                # Compose screens, components, themes
│   │   │   │   ├── data/              # Data sources and repository impl
│   │   │   │   ├── domain/            # UseCases, models, interfaces
│   │   │   │   └── util/              # Helpers, extensions
│   │   │   ├── res/                   # Resources (strings, drawables)
│   │   │   └── AndroidManifest.xml
├── build.gradle                      # Project-level Gradle
├── settings.gradle
└── .gitignore
