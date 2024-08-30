package de.ashman.ontrack.book.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookDto(
    @SerialName("key")
    val key: String,
    @SerialName("title")
    val title: String? = null,
    @SerialName("edition_count")
    val editionCount: Int? = null,
    @SerialName("cover_id")
    val coverId: Int? = null,
    @SerialName("cover_edition_key")
    val coverEditionKey: String? = null,
    @SerialName("subject")
    val subjects: List<String>? = null,
    @SerialName("ia_collection")
    val iaCollection: List<String>? = null,
    @SerialName("lendinglibrary")
    val lendingLibrary: Boolean? = null,
    @SerialName("printdisabled")
    val printDisabled: Boolean? = null,
    @SerialName("authors")
    val authors: List<AuthorDto>? = null,
    @SerialName("first_publish_year")
    val firstPublishYear: Int? = null,
    @SerialName("availability")
    val availability: BookAvailabilityDto? = null
)

@Serializable
data class AuthorDto(
    @SerialName("key")
    val key: String,
    @SerialName("name")
    val name: String? = null
)

@Serializable
data class BookAvailabilityDto(
    @SerialName("status")
    val status: String? = null,
    @SerialName("available_to_browse")
    val availableToBrowse: Boolean? = null,
    @SerialName("available_to_borrow")
    val availableToBorrow: Boolean? = null,
    @SerialName("available_to_waitlist")
    val availableToWaitlist: Boolean? = null,
    @SerialName("is_printdisabled")
    val isPrintDisabled: Boolean? = null,
    @SerialName("is_readable")
    val isReadable: Boolean? = null,
    @SerialName("is_lendable")
    val isLendable: Boolean? = null,
    @SerialName("is_previewable")
    val isPreviewable: Boolean? = null,
    @SerialName("identifier")
    val identifier: String? = null,
    @SerialName("isbn")
    val isbn: String? = null,
    @SerialName("oclc")
    val oclc: String? = null,
    @SerialName("openlibrary_work")
    val openLibraryWork: String? = null,
    @SerialName("openlibrary_edition")
    val openLibraryEdition: String? = null,
    @SerialName("last_loan_date")
    val lastLoanDate: String? = null,
    @SerialName("num_waitlist")
    val numWaitlist: Int? = null,
    @SerialName("last_waitlist_date")
    val lastWaitlistDate: String? = null,
    @SerialName("is_restricted")
    val isRestricted: Boolean? = null,
    @SerialName("is_browseable")
    val isBrowseable: Boolean? = null
)
