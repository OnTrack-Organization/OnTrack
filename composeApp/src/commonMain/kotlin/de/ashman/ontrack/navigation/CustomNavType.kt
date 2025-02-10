package de.ashman.ontrack.navigation

import androidx.core.bundle.Bundle
import androidx.navigation.NavType
import com.eygraber.uri.UriCodec
import de.ashman.ontrack.domain.tracking.Tracking
import kotlinx.serialization.json.Json

object CustomNavType {
    val TrackingNavType = object : NavType<Tracking>(
        isNullableAllowed = false
    ) {
        override fun get(bundle: Bundle, key: String): Tracking? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): Tracking {
            return Json.decodeFromString(UriCodec.decode(value))
        }

        override fun put(bundle: Bundle, key: String, value: Tracking) {
            bundle.putString(key, Json.encodeToString(value))
        }

        override fun serializeAsValue(value: Tracking): String {
            return UriCodec.encode(Json.encodeToString(value))
        }
    }
}