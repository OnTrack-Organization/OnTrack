package de.ashman.ontrack.api.movie

fun String?.getTMDBCoverUrl(): String = this?.let { "https://image.tmdb.org/t/p/original${it}" }.orEmpty()

fun String?.getYear(): String = this?.take(4).orEmpty()

fun getMovieDetailUrl(id: Int) = "https://www.themoviedb.org/movie/$id"

fun getShowDetailUrl(id: Int) = "https://www.themoviedb.org/tv/$id"