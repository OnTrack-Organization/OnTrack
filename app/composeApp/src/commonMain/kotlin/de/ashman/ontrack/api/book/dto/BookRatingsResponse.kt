package de.ashman.ontrack.api.book.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookRatingsResponse(
    val summary: RatingSummary,
    val counts: ReadingCounts,
)

@Serializable
data class RatingSummary(
    val average: Double? = null,
    val count: Int,
    val sortable: Double? = null,
)

// unused for now, variable names are wrong as well probably
@Serializable
data class ReadingCounts(
    @SerialName("1")
    val wantToRead: Int,
    @SerialName("2")
    val reading: Int,
    @SerialName("3")
    val read: Int,
    @SerialName("4")
    val readingAgain: Int,
    @SerialName("5")
    val lovedIt: Int,
)