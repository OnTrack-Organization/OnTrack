package de.ashman.ontrack.media.common.model

data class Genre(
    val id: Int,
    val name: String
)

data class ProductionCompany(
    val id: Int,
    val logoPath: String?,
    val name: String,
    val originCountry: String
)

data class ProductionCountry(
    val name: String
)

data class SpokenLanguage(
    val englishName: String,
    val name: String
)