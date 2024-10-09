package de.ashman.ontrack.media.album.api

suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
    return runCatching { apiCall() }
}