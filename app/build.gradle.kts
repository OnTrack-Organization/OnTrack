plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.crashlytics) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.buildKonfig) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.ksp) apply false
}

repositories {
    mavenCentral()
}
