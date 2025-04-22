# Keep Jetpack Compose classes
-keep class androidx.compose.** { *; }

# Keep Navigation Components
-keep class androidx.navigation.** { *; }
-dontwarn androidx.navigation.**

# Keep Material3 Components (if used)
-keep class androidx.compose.material3.** { *; }

# Keep Kotlin coroutines
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# Keep Ktor (if using HTTP requests)
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# Keep Compose-generated preview classes (optional - for debug builds)
-keep class androidx.compose.ui.tooling.** { *; }

# Keep Firebase (if using Firebase)
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Keep Kotlin Serialization metadata
-keep class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**

# Keep your domain models (MediaType, Media, etc.)
-keep class de.ashman.ontrack.domain.** { *; }
-keep class de.ashman.ontrack.navigation.* { *; }
-keep class de.ashman.ontrack.api.** { *; }
-keep class de.ashman.ontrack.entity.** { *; }

# Prevent obfuscation of @Serializable classes
-keepattributes *Annotation*
-keep class **Kt { *; }  # Keep Kotlin classes (important!)

# Keep enums (like MediaType) from being renamed
-keepclassmembers enum * { *; }

# Keep generated serializer classes
-keep class **$$serializer { *; }
-keep class **$Companion { *; }

# Keep Koin Dependency Injection classes
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# Keep Content Providers (Firebase and Koin might use them)
-keep class * extends android.content.ContentProvider { *; }

# *** KEY CHANGES FOR COMPOSE RESOURCES ***
-keep class ontrack.app.composeapp.generated.resources.** { *; }  # Keep generated resource classes
-keep class org.jetbrains.compose.resources.** { *; } # Keep Compose resource handling classes
-keep class androidx.compose.ui.res.** { *; } # Important for resource loading

# This is usually needed in a Compose project
-keep class kotlin.Metadata { *; }

-dontwarn org.jetbrains.compose.runtime.** # Suppress warnings related to Compose runtime

# Handle potential issues with Compose Material Icons
-keep class androidx.compose.material.icons.** { *; }
-keep class androidx.compose.material.icons.filled.** { *; }
-keep class androidx.compose.material.icons.outlined.** { *; }

# Handle room
-keep class * extends androidx.room.RoomDatabase { <init>(); }