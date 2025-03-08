package de.ashman.ontrack.navigation

import androidx.core.bundle.Bundle
import androidx.navigation.NavType
import com.eygraber.uri.UriCodec
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.user.User
import kotlinx.serialization.json.Json

object CustomNavType {
    val MediaNavigationItemsType = object : NavType<MediaNavigationItems>(
        isNullableAllowed = false
    ) {
        override fun get(bundle: Bundle, key: String): MediaNavigationItems? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): MediaNavigationItems {
            return Json.decodeFromString(UriCodec.decode(value))
        }

        override fun put(bundle: Bundle, key: String, value: MediaNavigationItems) {
            bundle.putString(key, Json.encodeToString(value))
        }

        override fun serializeAsValue(value: MediaNavigationItems): String {
            return UriCodec.encode(Json.encodeToString(value))
        }
    }

    val MediaTypeNavType = object : NavType<MediaType>(
        isNullableAllowed = false
    ) {
        override fun get(bundle: Bundle, key: String): MediaType? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): MediaType {
            return Json.decodeFromString(UriCodec.decode(value))
        }

        override fun put(bundle: Bundle, key: String, value: MediaType) {
            bundle.putString(key, Json.encodeToString(value))
        }

        override fun serializeAsValue(value: MediaType): String {
            return UriCodec.encode(Json.encodeToString(value))
        }
    }

    val UserType = object : NavType<User?>(
        isNullableAllowed = true
    ) {
        override fun get(bundle: Bundle, key: String): User? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): User? {
            return Json.decodeFromString(UriCodec.decode(value))
        }

        override fun put(bundle: Bundle, key: String, value: User?) {
            bundle.putString(key, Json.encodeToString(value))
        }

        override fun serializeAsValue(value: User?): String {
            return UriCodec.encode(Json.encodeToString(value))
        }
    }
}