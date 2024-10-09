package de.ashman.ontrack.media

fun String?.getTMDBCoverUrl(): String = this?.let { "https://image.tmdb.org/t/p/original${it}" }.orEmpty()

fun Int?.getOpenLibraryCoverUrl(): String = this?.let { "https://covers.openlibrary.org/b/id/${it}-L.jpg" }.orEmpty()

fun String?.getIGDBCoverUrl(): String = this?.let { "https://$it".replace("t_thumb", "t_1080p") }.orEmpty()
