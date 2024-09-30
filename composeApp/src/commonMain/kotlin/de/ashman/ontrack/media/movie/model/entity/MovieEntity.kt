package de.ashman.ontrack.media.movie.model.entity

import de.ashman.ontrack.media.common.model.GenreEntity
import de.ashman.ontrack.media.common.model.ProductionCompanyEntity
import de.ashman.ontrack.media.common.model.ProductionCountryEntity
import de.ashman.ontrack.media.common.model.SpokenLanguageDto
import de.ashman.ontrack.media.common.model.SpokenLanguageEntity
import de.ashman.ontrack.shelf.StatusType
import kotlinx.serialization.Serializable

@Serializable
data class MovieEntity(
    val adult: Boolean?,
    val backdropPath: String?,
    val belongsToCollection: CollectionEntity?,
    val budget: Long?,
    val genres: List<GenreEntity>?,
    val homepage: String?,
    val id: Int,
    val imdbId: String?,
    val originCountry: List<String>?,
    val originalLanguage: String?,
    val originalTitle: String?,
    val overview: String?,
    val popularity: Double?,
    val posterPath: String?,
    val productionCompanies: List<ProductionCompanyEntity>?,
    val productionCountries: List<ProductionCountryEntity>?,
    val releaseDate: String?,
    val revenue: Long?,
    val runtime: Int?,
    val spokenLanguages: List<SpokenLanguageEntity>?,
    val status: String?,
    val tagline: String?,
    val title: String,
    val video: Boolean?,
    val voteAverage: Double?,
    val voteCount: Int?,

    val watchStatus: StatusType,
)

@Serializable
data class CollectionEntity(
    val id: Int,
    val name: String,
    val posterPath: String,
    val backdropPath: String
)