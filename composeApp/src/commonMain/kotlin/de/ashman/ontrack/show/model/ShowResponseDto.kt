package de.ashman.ontrack.show.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShowResponseDto(
    @SerialName("results")
    val shows: List<ShowDto>? = null,

    @SerialName("page")
    val page: Int? = null,
    @SerialName("total_pages")
    val totalPages: Int? = null,
    @SerialName("total_results")
    val totalResults: Int? = null,
)