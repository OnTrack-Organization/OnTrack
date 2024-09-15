package de.ashman.ontrack.media.show.model

data class Show(
    val id: Int? = null,
    val name: String? = null,
    val originalName: String? = null,
    val originalLanguage: String? = null,

    val isAdult: Boolean? = null,
    val backdropPath: String? = null,
    val genreIds: List<Int>? = null,
    val originCountry: List<String>? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    val posterPath: String? = null,
    val firstAirDate: String? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null
)
