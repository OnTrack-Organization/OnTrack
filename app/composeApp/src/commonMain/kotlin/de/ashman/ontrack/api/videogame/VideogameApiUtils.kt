package de.ashman.ontrack.api.videogame

fun String?.getIGDBCoverUrl(): String = this?.let { "https://$it".replace("t_thumb", "t_1080p") }.orEmpty()
