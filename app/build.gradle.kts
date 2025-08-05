plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.hilt)
    alias(libs.plugins.google.services)

    kotlin("kapt")
}

android {
    namespace = "com.nuzio.newsapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nuzio.newsapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM manages Compose versions including Material3
    implementation(platform(libs.androidx.compose.bom))

    // Compose UI & Material3 without explicit versions (BOM controls)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    kapt(libs.hilt.compiler)
    implementation(libs.hilt.core)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.playServicesAuth)

    implementation("androidx.compose.material:material-icons-extended")  // Material icons (Material2 compatible icons)

    // Lifecycle ViewModel Compose and Hilt Navigation Compose
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // Coil for image loading in Compose
    implementation("io.coil-kt:coil-compose:2.5.0")

    // ViewModel Compose integration
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    // Retrofit + Moshi (networking + JSON parsing)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")

    // Room persistence library
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Paging 3 library with Compose integration
    implementation("androidx.paging:paging-compose:1.0.0-alpha18")
    implementation("androidx.paging:paging-runtime:3.1.1")

    // Dagger Hilt dependencies
    implementation("com.google.dagger:hilt-android:2.46.1")
    kapt("com.google.dagger:hilt-compiler:2.46.1")

    // Kotlin Coroutines for asynchronous programming
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Firebase Firestore and Analytics
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-analytics")

    // Facebook Login SDK
    implementation("com.facebook.android:facebook-login:16.3.0")

    // Accompanist Navigation Animation
    implementation("com.google.accompanist:accompanist-navigation-animation:0.30.1")

    // Testing dependencies
    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.mockk:mockk:1.13.7")
    testImplementation("app.cash.turbine:turbine:0.12.1")

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
