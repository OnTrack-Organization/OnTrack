package de.ashman.ontrack.api.album

suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
    return runCatching { apiCall() }
}