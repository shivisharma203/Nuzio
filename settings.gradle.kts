pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
        plugins {
            // Define versions for plugins used in the project
         // id("com.google.gms.google-services") version "4.4.1" apply false // Or the latest version
        }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Nuzio"
include(":app")
