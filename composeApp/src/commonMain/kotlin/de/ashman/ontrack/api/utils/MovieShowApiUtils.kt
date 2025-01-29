package de.ashman.ontrack.api.utils

fun String?.getTMDBCoverUrl(): String = this?.let { "https://image.tmdb.org/t/p/original${it}" }.orEmpty()

fun String?.getYear(): String = this?.take(4).orEmpty()