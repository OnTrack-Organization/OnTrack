package de.ashman.ontrack.media.common.model

import kotlinx.serialization.Serializable

// TV SHOW / MOVIE COMMON
@Serializable
data class GenreDto(
    val id: Int,
    val name: String
)

@Serializable
data class ProductionCompanyDto(
    val id: Int,
    val logoPath: String? = null,
    val name: String,
    val originCountry: String
)

@Serializable
data class ProductionCountryDto(
    val name: String
)

@Serializable
data class SpokenLanguageDto(
    val englishName: String,
    val name: String
)