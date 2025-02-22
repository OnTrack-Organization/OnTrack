
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.serialization)
    alias(libs.plugins.buildKonfig)
    alias(libs.plugins.crashlytics)
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

kotlin {
    compilerOptions {
        targets.configureEach {
            compilations.configureEach {
                compileTaskProvider.get().compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }
            }
        }
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            freeCompilerArgs += "-Xbinary=bundleId=de.ashman.ontrack.ComposeApp"

            export("io.github.mirzemehdi:kmpnotifier:1.4.0")
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.filekit)

            implementation(libs.compose.navigation)

            implementation(libs.coil.compose)
            implementation(libs.coil.network)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.kotlinx.datetime)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            implementation(libs.ktor.core)
            implementation(libs.ktor.contentNegotiation)
            implementation(libs.ktor.json)

            implementation(libs.xmlutil.core)
            implementation(libs.xmlutil.serialization)

            implementation(libs.uri.kmp)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.kermit)

            implementation(libs.bundles.ksoup)
            implementation(libs.bundles.kmpauth)
            implementation(libs.bundles.firebase)

            api(libs.kmpnotifier)
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)

            implementation(libs.ktor.android)

            implementation(project.dependencies.platform(libs.firebase.bom))
        }

        iosMain.dependencies {
            implementation(libs.ktor.darwin)
        }
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            freeCompilerArgs.addAll(
                "-P",
                "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=de.ashman.ontrack.navigation.CommonParcelize"
            )
        }
    }
}

android {
    namespace = "de.ashman.ontrack"
    compileSdk = 35

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "de.ashman.ontrack"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                file("proguard-rules.pro")
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    dependencies {
        debugImplementation(compose.uiTooling)
    }
}

buildkonfig {
    packageName = "de.ashman.ontrack"

    defaultConfigs {
        buildConfigField(
            STRING,
            "GOOGLE_AUTH_CLIENT_ID",
            gradleLocalProperties(rootDir, providers).getProperty("google_auth_client_id").orEmpty()
        )
    }
    defaultConfigs {
        buildConfigField(
            STRING,
            "TMDB_API_KEY",
            gradleLocalProperties(rootDir, providers).getProperty("tmdb_api_key").orEmpty()
        )
    }
    defaultConfigs {
        buildConfigField(
            STRING,
            "TWITCH_CLIENT_ID",
            gradleLocalProperties(rootDir, providers).getProperty("twitch_client_id").orEmpty()
        )
    }
    defaultConfigs {
        buildConfigField(
            STRING,
            "TWITCH_CLIENT_SECRET",
            gradleLocalProperties(rootDir, providers).getProperty("twitch_client_secret").orEmpty()
        )
    }
    defaultConfigs {
        buildConfigField(
            STRING,
            "SPOTIFY_CLIENT_ID",
            gradleLocalProperties(rootDir, providers).getProperty("spotify_client_id").orEmpty()
        )
    }
    defaultConfigs {
        buildConfigField(
            STRING,
            "SPOTIFY_CLIENT_SECRET",
            gradleLocalProperties(rootDir, providers).getProperty("spotify_client_secret").orEmpty()
        )
    }
}
