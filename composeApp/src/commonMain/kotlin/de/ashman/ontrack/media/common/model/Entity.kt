package de.ashman.ontrack.media.common.model

import kotlinx.serialization.Serializable

@Serializable
data class GenreEntity(
    val id: Int,
    val name: String
)

@Serializable
data class ProductionCompanyEntity(
    val id: Int,
    val logoPath: String?,
    val name: String,
    val originCountry: String
)

@Serializable
data class ProductionCountryEntity(
    val name: String
)

@Serializable
data class SpokenLanguageEntity(
    val englishName: String,
    val name: String
)