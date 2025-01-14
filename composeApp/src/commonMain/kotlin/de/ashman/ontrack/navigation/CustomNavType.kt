package de.ashman.ontrack.navigation

import androidx.core.bundle.Bundle
import androidx.navigation.NavType
import com.eygraber.uri.UriCodec
import de.ashman.ontrack.domain.Media
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object CustomNavType {
    val MediaNavType = object : NavType<Media>(
        isNullableAllowed = false
    ) {
        override fun get(bundle: Bundle, key: String): Media? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): Media {
            return Json.decodeFromString(UriCodec.decode(value))
        }

        override fun put(bundle: Bundle, key: String, value: Media) {
            bundle.putString(key, Json.encodeToString(value))
        }

        override fun serializeAsValue(value: Media): String {
            return UriCodec.encode(Json.encodeToString(value))
        }
    }
}