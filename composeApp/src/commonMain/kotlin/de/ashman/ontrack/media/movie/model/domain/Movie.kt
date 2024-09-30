package de.ashman.ontrack.media.movie.model.domain

import de.ashman.ontrack.media.common.model.Genre
import de.ashman.ontrack.media.common.model.ProductionCompany
import de.ashman.ontrack.media.common.model.ProductionCountry
import de.ashman.ontrack.media.common.model.SpokenLanguage
import de.ashman.ontrack.shelf.StatusType

data class Movie(
    val adult: Boolean?,
    val backdropPath: String?,
    val belongsToCollection: Collection?,
    val budget: Long?,
    val genres: List<Genre>?,
    val homepage: String?,
    val id: Int,
    val imdbId: String?,
    val originCountry: List<String>?,
    val originalLanguage: String?,
    val originalTitle: String?,
    val overview: String?,
    val popularity: Double?,
    val posterPath: String?,
    val productionCompanies: List<ProductionCompany>?,
    val productionCountries: List<ProductionCountry>?,
    val releaseDate: String?,
    val revenue: Long?,
    val runtime: Int?,
    val spokenLanguages: List<SpokenLanguage>?,
    val status: String?,
    val tagline: String?,
    val title: String,
    val video: Boolean?,
    val voteAverage: Double?,
    val voteCount: Int?,

    val watchStatus: StatusType = StatusType.WATCHED,
)

data class Collection(
    val id: Int,
    val name: String,
    val posterPath: String,
    val backdropPath: String
)