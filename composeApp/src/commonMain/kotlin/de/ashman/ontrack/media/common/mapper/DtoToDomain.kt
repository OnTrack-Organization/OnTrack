package de.ashman.ontrack.media.common.mapper

import de.ashman.ontrack.media.common.model.Genre
import de.ashman.ontrack.media.common.model.GenreDto
import de.ashman.ontrack.media.common.model.ProductionCompany
import de.ashman.ontrack.media.common.model.ProductionCompanyDto
import de.ashman.ontrack.media.common.model.ProductionCountry
import de.ashman.ontrack.media.common.model.ProductionCountryDto
import de.ashman.ontrack.media.common.model.SpokenLanguage
import de.ashman.ontrack.media.common.model.SpokenLanguageDto

fun GenreDto.toDomain(): Genre {
    return Genre(id = id, name = name)
}

fun ProductionCompanyDto.toDomain(): ProductionCompany {
    return ProductionCompany(id = id, logoPath = logoPath, name = name, originCountry = originCountry)
}

fun ProductionCountryDto.toDomain(): ProductionCountry {
    return ProductionCountry(name = name)
}

fun SpokenLanguageDto.toDomain(): SpokenLanguage {
    return SpokenLanguage(
        englishName = englishName,
        name = name
    )
}