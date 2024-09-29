package de.ashman.ontrack.media.show.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShowResponseDto(
    @SerialName("results")
    val shows: List<ShowDto> = emptyList(),

    val page: Int,
    val totalPages: Int,
    val totalResults: Int,
)