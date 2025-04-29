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
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

version = "2.0.1"

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
        versionCode = 11
        versionName = version.toString()
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

            ndk {
                debugSymbolLevel = "FULL"
            }
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

            export(libs.kmpnotifier)
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
            implementation(libs.filekit)
            implementation(libs.compose.shimmer)
            implementation(libs.jetlime.get().toString()) {
                exclude(group = "org.jetbrains.dokka")
            }
            implementation(libs.zoomable)

            implementation(libs.coil.compose)
            implementation(libs.coil.network)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.kotlinx.datetime)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            implementation(libs.bundles.ktor)

            implementation(libs.xmlutil.core)
            implementation(libs.xmlutil.serialization)

            implementation(libs.uri.kmp)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.kermit)

            implementation(libs.bundles.ksoup)
            implementation(libs.bundles.kmpauth)
            implementation(libs.bundles.firebase)

            api(libs.kmpnotifier)

            implementation(libs.datastore)
            implementation(libs.datastore.preferences)

            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)

            implementation(libs.ktor.android)

            implementation(project.dependencies.platform(libs.firebase.bom))

            implementation(libs.androidx.sqlite.framework)
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

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

buildkonfig {
    packageName = "de.ashman.ontrack"

    defaultConfigs {
        buildConfigField(
            type = STRING,
            name = "APP_VERSION",
            value = version.toString()
        )

        buildConfigField(
            STRING,
            "GOOGLE_AUTH_CLIENT_ID",
            gradleLocalProperties(projectDir, providers).getProperty("google_auth_client_id").orEmpty()
        )
        buildConfigField(
            STRING,
            "TMDB_API_KEY",
            gradleLocalProperties(projectDir, providers).getProperty("tmdb_api_key").orEmpty()
        )
        buildConfigField(
            STRING,
            "TWITCH_CLIENT_ID",
            gradleLocalProperties(projectDir, providers).getProperty("twitch_client_id").orEmpty()
        )
        buildConfigField(
            STRING,
            "TWITCH_CLIENT_SECRET",
            gradleLocalProperties(projectDir, providers).getProperty("twitch_client_secret").orEmpty()
        )
        buildConfigField(
            STRING,
            "SPOTIFY_CLIENT_ID",
            gradleLocalProperties(projectDir, providers).getProperty("spotify_client_id").orEmpty()
        )
        buildConfigField(
            STRING,
            "SPOTIFY_CLIENT_SECRET",
            gradleLocalProperties(projectDir, providers).getProperty("spotify_client_secret").orEmpty()
        )
    }
}
