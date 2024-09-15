package de.ashman.ontrack.media.book.model

data class Book(
    val key: String,
    val title: String?,
    val editionCount: Int?,
    val coverId: Int?,
    val coverEditionKey: String?,
    val subject: List<String>?,
    val iaCollection: List<String>?,
    val lendingLibrary: Boolean?,
    val printDisabled: Boolean?,
    val authors: List<Author>?,
    val firstPublishYear: Int?,
    val availability: BookAvailability?
)

data class Author(
    val key: String,
    val name: String?
)

data class BookAvailability(
    val status: String?,
    val availableToBrowse: Boolean?,
    val availableToBorrow: Boolean?,
    val availableToWaitlist: Boolean?,
    val isPrintDisabled: Boolean?,
    val isReadable: Boolean?,
    val isLendable: Boolean?,
    val isPreviewable: Boolean?,
    val identifier: String?,
    val isbn: String?,
    val oclc: String?,
    val openLibraryWork: String?,
    val openLibraryEdition: String?,
    val lastLoanDate: String?,
    val numWaitlist: Int?,
    val lastWaitlistDate: String?,
    val isRestricted: Boolean?,
    val isBrowseable: Boolean?
)
