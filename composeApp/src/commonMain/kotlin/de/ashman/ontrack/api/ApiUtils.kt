package de.ashman.ontrack.api

import co.touchlab.kermit.Logger

suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
    return try {
        val result = apiCall()
        Logger.d { "API call successful: $result" }
        Result.success(result)
    } catch (e: Exception) {
        Logger.e(e) { "API call failed: $e" }
        Result.failure(e)
    }
}

fun String?.getTMDBCoverUrl(): String = this?.let { "https://image.tmdb.org/t/p/original${it}" }.orEmpty()

fun Int?.getOpenLibraryCoverUrl(): String = this?.let { "https://covers.openlibrary.org/b/id/${it}-L.jpg" }.orEmpty()

fun String?.getIGDBCoverUrl(): String = this?.let { "https://$it".replace("t_thumb", "t_1080p") }.orEmpty()