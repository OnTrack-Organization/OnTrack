package de.ashman.ontrack.media.common.mapper

import de.ashman.ontrack.media.common.model.Genre
import de.ashman.ontrack.media.common.model.GenreEntity
import de.ashman.ontrack.media.common.model.ProductionCompany
import de.ashman.ontrack.media.common.model.ProductionCompanyEntity
import de.ashman.ontrack.media.common.model.ProductionCountry
import de.ashman.ontrack.media.common.model.ProductionCountryEntity
import de.ashman.ontrack.media.common.model.SpokenLanguage
import de.ashman.ontrack.media.common.model.SpokenLanguageEntity

fun Genre.toEntity(): GenreEntity {
    return GenreEntity(id = id, name = name)
}

fun ProductionCompany.toEntity(): ProductionCompanyEntity {
    return ProductionCompanyEntity(id = id, logoPath = logoPath, name = name, originCountry = originCountry)
}

fun ProductionCountry.toEntity(): ProductionCountryEntity {
    return ProductionCountryEntity(name = name)
}

fun SpokenLanguage.toEntity(): SpokenLanguageEntity {
    return SpokenLanguageEntity(
        englishName = englishName,
        name = name
    )
}