package de.ashman.ontrack.api.utils

fun String?.getIGDBCoverUrl(): String = this?.let { "https://$it".replace("t_thumb", "t_1080p") }.orEmpty()
