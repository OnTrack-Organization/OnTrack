package de.ashman.ontrack.media.book.model

import kotlinx.serialization.Serializable

@Serializable
data class BookDto(
    val key: String,
    val title: String? = null,
    val editionCount: Int? = null,
    val coverId: Int? = null,
    val coverEditionKey: String? = null,
    val subject: List<String>? = null,
    val iaCollection: List<String>? = null,
    val lendingLibrary: Boolean? = null,
    val printDisabled: Boolean? = null,
    val authors: List<AuthorDto>? = null,
    val firstPublishYear: Int? = null,
    val availability: BookAvailabilityDto? = null
)

@Serializable
data class AuthorDto(
    val key: String,
    val name: String? = null
)

@Serializable
data class BookAvailabilityDto(
    val status: String? = null,
    val availableToBrowse: Boolean? = null,
    val availableToBorrow: Boolean? = null,
    val availableToWaitlist: Boolean? = null,
    val isPrintDisabled: Boolean? = null,
    val isReadable: Boolean? = null,
    val isLendable: Boolean? = null,
    val isPreviewable: Boolean? = null,
    val identifier: String? = null,
    val isbn: String? = null,
    val oclc: String? = null,
    val openLibraryWork: String? = null,
    val openLibraryEdition: String? = null,
    val lastLoanDate: String? = null,
    val numWaitlist: Int? = null,
    val lastWaitlistDate: String? = null,
    val isRestricted: Boolean? = null,
    val isBrowseable: Boolean? = null
)
