import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.serialization)
    alias(libs.plugins.buildKonfig)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
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
            implementation(libs.compose.navigation)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.coil.compose)
            implementation(libs.coil.network)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.kotlinx.datetime)

           /* runtimeOnly(libs.androidx.datastore.preferences.core)
            implementation("androidx.datastore:datastore:1.1.1")
            runtimeOnly("androidx.datastore:datastore-preferences:1.1.1")
            implementation("androidx.datastore:datastore-core:1.1.1")*/

            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            implementation(libs.firebase.gitlive.auth)
            implementation(libs.firebase.gitlive.database)
            implementation(libs.kmpauth.google)
            implementation(libs.kmpauth.firebase)
            implementation(libs.kmpauth.uihelper)

            implementation(libs.ktor.core)
            implementation(libs.ktor.contentNegotiation)
            implementation(libs.ktor.json)

            implementation(libs.xmlutil.core)
            implementation(libs.xmlutil.serialization)

            implementation("network.chaintech:compose-multiplatform-media-player:1.0.22")

            //implementation(libs.androidx.paging)
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)

            implementation(libs.ktor.android)
        }

        iosMain.dependencies {
            implementation(libs.ktor.darwin)
        }
    }
}

android {
    namespace = "de.ashman.ontrack"
    compileSdk = 34

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "de.ashman.ontrack"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
            gradleLocalProperties(rootDir).getProperty("google_auth_client_id") ?: ""
        )
    }
    defaultConfigs {
        buildConfigField(
            STRING,
            "TMDB_API_KEY",
            gradleLocalProperties(rootDir).getProperty("tmdb_api_key") ?: ""
        )
    }
    defaultConfigs {
        buildConfigField(
            STRING,
            "TWITCH_CLIENT_ID",
            gradleLocalProperties(rootDir).getProperty("twitch_client_id") ?: ""
        )
    }
    defaultConfigs {
        buildConfigField(
            STRING,
            "TWITCH_CLIENT_SECRET",
            gradleLocalProperties(rootDir).getProperty("twitch_client_secret") ?: ""
        )
    }
    defaultConfigs {
        buildConfigField(
            STRING,
            "SPOTIFY_CLIENT_ID",
            gradleLocalProperties(rootDir).getProperty("spotify_client_id") ?: ""
        )
    }
    defaultConfigs {
        buildConfigField(
            STRING,
            "SPOTIFY_CLIENT_SECRET",
            gradleLocalProperties(rootDir).getProperty("spotify_client_secret") ?: ""
        )
    }
}
