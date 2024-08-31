package de.ashman.ontrack.show.model

import kotlinx.serialization.Serializable

@Serializable
data class ShowDto(
    val id: Int,
    val name: String? = null,
    val originalName: String? = null,
    val originalLanguage: String? = null,

    val adult: Boolean? = null,
    val backdropPath: String? = null,
    val genreIds: List<Int>? = null,
    val originCountry: List<String>? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    val posterPath: String? = null,
    val firstAirDate: String? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
)