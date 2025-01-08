package de.ashman.ontrack.api

suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
    return runCatching { apiCall() }
}

fun String?.getTMDBCoverUrl(): String = this?.let { "https://image.tmdb.org/t/p/original${it}" }.orEmpty()

fun Int?.getOpenLibraryCoverUrl(): String = this?.let { "https://covers.openlibrary.org/b/id/${it}-L.jpg" }.orEmpty()

fun String?.getIGDBCoverUrl(): String = this?.let { "https://$it".replace("t_thumb", "t_1080p") }.orEmpty()