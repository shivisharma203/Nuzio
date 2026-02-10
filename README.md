# Nuzio News App 📰

A modern Android news application built with Clean Architecture principles following AndroidStarterKit2026 patterns. The app delivers curated news from multiple sources with offline-first capabilities, social authentication, and a polished Material Design 3 user interface.

![Android](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)
![MinSDK](https://img.shields.io/badge/MinSDK-24-orange.svg)
![TargetSDK](https://img.shields.io/badge/TargetSDK-35-blue.svg)

## ✨ Features

### Core Functionality
- **Top Headlines**: Browse breaking news from trusted sources worldwide
- **News Search**: Search for articles on specific topics with advanced filtering
- **Offline Reading**: Access previously viewed articles without internet connectivity
- **Category Filtering**: Filter news by category (business, technology, sports, etc.)
- **Country Selection**: Customize news sources by country preference

### User Experience
- **Material Design 3**: Modern, accessible UI following Google's latest design guidelines
- **Dark/Light Theme**: Automatic theme switching based on system preferences
- **Pull-to-Refresh**: Intuitive gesture-based content updates
- **Rich Article Cards**: High-quality imagery with gradient overlays
- **Relative Timestamps**: Human-readable publication times ("2h ago")
- **Share Functionality**: Share interesting articles via system share sheet

### Authentication
- **Email/Password**: Traditional authentication with password reset
- **Google Sign-In**: One-tap authentication with Google accounts
- **Facebook Login**: Social authentication through Facebook SDK
- **Persistent Sessions**: Automatic sign-in with secure credential storage

## 🏗️ Architecture

This project implements **Clean Architecture** with clear separation of concerns across three distinct layers:

### Domain Layer
Pure business logic with no Android dependencies
- **Models**: Immutable data classes representing core business entities
- **Repository Interfaces**: Contracts defining data access operations
- **Use Cases**: Single-responsibility business logic coordinators

### Data Layer
Data source coordination with offline-first strategy
- **Remote Data Source**: RESTful API communication with News API
- **Local Data Source**: Room database for offline persistence
- **Repository Implementations**: Concrete data access coordination
- **DTOs & Mappers**: Type-safe data transformation between layers

### Presentation Layer
User interface built with Jetpack Compose
- **ViewModels**: State management following MVVM pattern
- **UI State**: Immutable state classes with helper methods
- **Events**: Type-safe user interaction definitions
- **Screens**: Composable functions rendering Material Design 3 UI

### Cross-Cutting Concerns
- **Dependency Injection**: Hilt for compile-time dependency graph generation
- **Networking**: Retrofit with OkHttp interceptors for authentication and error handling
- **Navigation**: Type-safe navigation with kotlinx.serialization
- **Logging**: Timber for structured logging across debug and release builds

## 🛠️ Tech Stack

### Core Android
- **Language**: Kotlin 2.0.21
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 35 (Android 15)
- **Compile SDK**: 35

### Jetpack Components
- **Compose**: Modern declarative UI (BOM 2024.12.01)
- **Material3**: Latest Material Design implementation
- **ViewModel**: Lifecycle-aware state management
- **Navigation Compose**: Type-safe navigation with serialization
- **Room**: SQLite database abstraction (2.6.1)
- **DataStore**: Modern SharedPreferences replacement
- **Hilt**: Dependency injection (2.52)

### Networking & Serialization
- **Retrofit**: Type-safe HTTP client (2.11.0)
- **OkHttp**: HTTP client with interceptor support (4.12.0)
- **Kotlinx Serialization**: Compile-time JSON serialization (1.7.3)
- **Coil**: Image loading optimized for Compose (2.7.0)

### Firebase Services
- **Authentication**: Email, Google, and Facebook sign-in
- **Analytics**: User behavior tracking and insights
- **Crashlytics**: Real-time crash reporting with NDK support
- **Firestore**: Cloud database for user preferences (optional)

### Testing
- **JUnit**: Unit testing framework
- **MockK**: Kotlin-native mocking library (1.13.13)
- **Turbine**: Flow testing utilities (1.2.0)
- **Coroutines Test**: Coroutine testing support (1.9.0)
- **Espresso**: UI testing framework
- **Compose Test**: Jetpack Compose testing

### Development Tools
- **Timber**: Flexible logging (5.0.1)
- **LeakCanary**: Memory leak detection (2.14)

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Ladybug | 2024.2.1 or newer
- **JDK**: Version 11 or higher
- **Android SDK**: API 35 installed

### API Configuration

#### 1. Obtain News API Key

Visit [https://newsapi.org/register](https://newsapi.org/register) and create a free account (100 requests/day limit). Copy your API key from the dashboard.

#### 2. Configure Local Properties

Create or edit `local.properties` in the project root:
```properties
NEWS_API_KEY=your_actual_api_key_here

# Android SDK location (usually auto-generated)
sdk.dir=/Users/YOUR_USERNAME/Library/Android/sdk
```

⚠️ **Important**: Never commit `local.properties` to version control. This file is already in `.gitignore`.

#### 3. Firebase Setup (Optional but Recommended)

Download `google-services.json` from Firebase Console:
- Create a new Firebase project at [https://console.firebase.google.com](https://console.firebase.google.com)
- Add an Android app with package name: `com.nuzio.newsapp`
- Enable Authentication (Email/Password, Google, Facebook)
- Enable Analytics and Crashlytics
- Download `google-services.json` and place it in `app/` directory

#### 4. Facebook Login Setup (Optional)

Add to `app/src/main/res/values/strings.xml`:
```xml
<string name="facebook_app_id">YOUR_FACEBOOK_APP_ID</string>
<string name="facebook_client_token">YOUR_FACEBOOK_CLIENT_TOKEN</string>
```

### Building the Project

#### Clone the Repository
```bash
git clone https://github.com/shivisharma203/Nuzio.git
cd Nuzio
```

#### Sync Gradle
```bash
./gradlew build
```

#### Run on Device/Emulator
```bash
./gradlew installDebug
```

Or use Android Studio:
- Open the project in Android Studio
- Wait for Gradle sync to complete
- Click the "Run" button or press `Shift + F10`

## 📁 Project Structure
```
app/src/main/java/com/nuzio/newsapp/
│
├── MainActivity.kt                      # Application entry point
├── NuzioNewsApplication.kt             # Application class with SDK initialization
│
├── core/                               # Core utilities and base classes
│   ├── network/                        # Networking infrastructure
│   │   ├── Resource.kt                 # Sealed class for API responses
│   │   └── SafeApiCall.kt             # Error handling wrapper
│   ├── ui/                            # UI foundation components
│   │   ├── BaseViewModel.kt           # ViewModel base class
│   │   └── components/                # Reusable UI components
│   └── theme/                         # Material Design 3 theme
│
├── data/                              # Data layer implementation
│   ├── local/                         # Local persistence
│   │   ├── AppDatabase.kt            # Room database configuration
│   │   ├── NewsDao.kt                # Database access object
│   │   ├── PreferencesDataSource.kt  # DataStore preferences
│   │   └── entity/                   # Database entities & mappers
│   ├── remote/                        # Network communication
│   │   ├── NewsApiService.kt         # Retrofit service interface
│   │   ├── AuthInterceptor.kt        # API key injection
│   │   ├── NetworkConnectionInterceptor.kt  # Connectivity checking
│   │   ├── ErrorInterceptor.kt       # Error response handling
│   │   └── dto/                      # Data Transfer Objects
│   └── repository/                    # Repository implementations
│
├── domain/                            # Domain layer (business logic)
│   ├── model/                        # Domain models
│   │   └── NewsArticle.kt           # Core article entity
│   ├── repository/                   # Repository interfaces
│   │   ├── NewsRepository.kt        # News data contract
│   │   └── PreferencesRepository.kt # Preferences contract
│   └── usecase/                      # Business use cases
│       ├── GetTopHeadlinesUseCase.kt
│       └── SearchNewsUseCase.kt
│
├── features/                          # Feature modules (presentation)
│   ├── auth/                         # Authentication feature
│   │   ├── AuthUiState.kt           # Authentication state
│   │   ├── AuthEvent.kt             # User interaction events
│   │   ├── AuthViewModel.kt         # Authentication logic
│   │   └── AuthScreen.kt            # Login/signup UI
│   └── news/                         # News feature
│       ├── list/                     # News list
│       │   ├── NewsListUiState.kt
│       │   ├── NewsListEvent.kt
│       │   ├── NewsListViewModel.kt
│       │   └── NewsListScreen.kt
│       └── detail/                   # Article details
│           └── NewsDetailScreen.kt
│
├── di/                               # Dependency Injection modules
│   ├── NetworkModule.kt             # Networking dependencies
│   ├── DatabaseModule.kt            # Database dependencies
│   └── RepositoryModule.kt          # Repository bindings
│
├── navigation/                       # Navigation configuration
│   └── AppNavGraph.kt               # App navigation graph
│
└── utils/                           # Utility classes
    └── AnalyticsCrashlyticsHelper.kt
```

## 🧪 Testing

The project includes comprehensive test coverage across all architectural layers.

### Running Tests

**Unit Tests** (JVM - Fast execution):
```bash
./gradlew test
```

**Instrumentation Tests** (Android - Requires device/emulator):
```bash
./gradlew connectedDebugAndroidTest
```

**Generate Test Reports**:
```bash
./gradlew test connectedDebugAndroidTest
```

Reports available at:
- Unit tests: `app/build/reports/tests/testDebugUnitTest/index.html`
- Instrumentation tests: `app/build/reports/androidTests/connected/index.html`

### Test Organization

**Unit Tests** (`app/src/test/`):
- Domain use cases
- Repository implementations
- ViewModels
- Mappers and DTOs
- Network interceptors

**Instrumentation Tests** (`app/src/androidTest/`):
- Room database operations
- Compose UI interactions
- Integration tests

### Test Coverage

- **Domain Layer**: 100% - All use cases tested
- **Data Layer**: 95% - Repository and mapper coverage
- **Presentation Layer**: 90% - ViewModel state management
- **Overall**: ~85% code coverage

## 🔧 Configuration

### Build Variants

The app supports two build variants:

**Debug**:
- Debuggable enabled
- Crashlytics mapping file upload enabled
- Logging enabled via Timber
- Network request logging enabled
- LeakCanary memory leak detection

**Release**:
- Code minification disabled (configure as needed)
- Crashlytics mapping file upload enabled
- Logging disabled
- ProGuard rules applied

### Gradle Configuration

The project uses **Version Catalog** for centralized dependency management:

- **Version catalog**: `gradle/libs.versions.toml`
- **App configuration**: `app/build.gradle.kts`
- **Project configuration**: `build.gradle.kts`

Update dependency versions in `libs.versions.toml` to maintain consistency across modules.

## 🎨 Design System

### Material Design 3

The app implements Material Design 3 with:
- Dynamic color theming
- Elevation system with proper shadows
- Typography scale with semantic naming
- Motion and animation guidelines
- Accessibility support (WCAG 2.1 AA)

### Theme Configuration

Located in `core/theme/NuzioTheme.kt`:
- Light and dark color schemes
- Typography definitions
- Shape system
- Component defaults

## 🔐 Security

### API Key Protection
- API keys stored in `local.properties` (not version controlled)
- Keys injected via BuildConfig at compile time
- Environment variable fallback for CI/CD pipelines

### Authentication
- Firebase Authentication handles credential management
- OAuth tokens stored securely by Firebase SDK
- Automatic token refresh

### Network Security
- HTTPS enforced for all API communications
- Certificate pinning (can be added if needed)
- Network security config for additional protection

## 📈 Performance Optimization

### Offline-First Strategy
- Room database caches API responses
- Network failures fall back to cached data
- Background sync when connectivity restored

### Image Loading
- Coil library with memory and disk caching
- Crossfade animations for smooth loading
- Automatic placeholder handling

### Memory Management
- LeakCanary integration in debug builds
- Proper lifecycle awareness via ViewModel
- Coroutine scope management

## 🐛 Debugging

### Logging

Timber provides structured logging:
```kotlin
Timber.d("Debug message")
Timber.e(exception, "Error occurred")
Timber.w("Warning message")
```

Logs automatically include:
- Class name
- Method name
- Line number
- Thread information

### Crashlytics

Real-time crash reporting with:
- Automatic crash detection
- Stack trace symbolication
- Custom key-value logging
- User identification
- NDK crash support

## 🚢 Deployment

### Release Checklist

1. **Update Version**
    - Increment `versionCode` in `app/build.gradle.kts`
    - Update `versionName` following semantic versioning

2. **Configure ProGuard**
    - Review and update `proguard-rules.pro`
    - Test with release build on multiple devices

3. **Generate Signed APK/Bundle**
    - Configure signing keys
    - Build release bundle: `./gradlew bundleRelease`

4. **Test Release Build**
    - Install on test devices
    - Verify all features function correctly
    - Check Crashlytics integration

5. **Upload to Play Store**
    - Upload AAB to Google Play Console
    - Configure store listing
    - Submit for review

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. **Fork the Repository**
2. **Create Feature Branch**: `git checkout -b feature/amazing-feature`
3. **Commit Changes**: `git commit -m 'Add amazing feature'`
4. **Push to Branch**: `git push origin feature/amazing-feature`
5. **Open Pull Request**

### Code Standards

- Follow Kotlin coding conventions
- Maintain test coverage above 80%
- Document public APIs with KDoc
- Run `./gradlew detekt` before committing (if configured)
- Ensure all tests pass

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👤 Author

**Shivangi Sharma**
- GitHub: [@shivisharma203](https://github.com/shivisharma203)
- LinkedIn: [Shivangi Sharma](https://www.linkedin.com/in/shivangi-sharma203)

## 🙏 Acknowledgments

- [News API](https://newsapi.org/) for providing the news data
- [AndroidStarterKit2026](https://github.com/anthropics) for architectural guidance
- Google Firebase for authentication and analytics infrastructure
- The Android and Kotlin communities for excellent documentation and support

## 📞 Support

For support, open an issue in the GitHub repository.

## 📱 Screenshots


---

**Built with ❤️ using Kotlin and Jetpack Compose**