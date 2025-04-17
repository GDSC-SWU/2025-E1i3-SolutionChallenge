pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        // ✅ 여기에 추가해야 함
        id("com.google.gms.google-services") version "4.4.0"
        id("org.jetbrains.kotlin.android") version "1.9.0"
        id("org.jetbrains.kotlin.android") version "1.9.0"
        id("org.jetbrains.kotlin.android") version "1.9.0"
        id("org.jetbrains.kotlin.android") version "1.9.0"
        id("org.jetbrains.kotlin.android") version "1.9.0"
        id("org.jetbrains.kotlin.android") version "1.9.0"
        id("org.jetbrains.kotlin.android") version "1.9.0"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "solutionchallenge"
include(":app")
