package de.ashman.ontrack.media.movie.model.dto

import de.ashman.ontrack.media.common.model.GenreDto
import de.ashman.ontrack.media.common.model.ProductionCompanyDto
import de.ashman.ontrack.media.common.model.ProductionCountryDto
import de.ashman.ontrack.media.common.model.SpokenLanguageDto
import kotlinx.serialization.Serializable

@Serializable
data class MovieDto(
    val adult: Boolean? = null,
    val backdropPath: String? = null,
    val belongsToCollection: CollectionDto? = null,
    val budget: Long? = null,
    val genres: List<GenreDto>? = null,
    val homepage: String? = null,
    val id: Int,
    val imdbId: String? = null,
    val originCountry: List<String>? = null,
    val originalLanguage: String? = null,
    val originalTitle: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    val posterPath: String? = null,
    val productionCompanies: List<ProductionCompanyDto>? = null,
    val productionCountries: List<ProductionCountryDto>? = null,
    val releaseDate: String? = null,
    val revenue: Long? = null,
    val runtime: Int? = null,
    val spokenLanguages: List<SpokenLanguageDto>? = null,
    val status: String? = null,
    val tagline: String? = null,
    val title: String,
    val video: Boolean? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
)

@Serializable
data class CollectionDto(
    val id: Int,
    val name: String,
    val posterPath: String,
    val backdropPath: String
)
