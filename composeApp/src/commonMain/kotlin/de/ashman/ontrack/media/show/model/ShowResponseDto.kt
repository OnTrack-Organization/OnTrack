package de.ashman.ontrack.media.show.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShowResponseDto(
    @SerialName("results")
    val shows: List<ShowDto>? = null,

    val page: Int? = null,
    val totalPages: Int? = null,
    val totalResults: Int? = null,
)