package de.ashman.ontrack.api.boardgame.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BoardgameResponseDto(
    @SerialName("items")
    val boardgames: List<BoardgameDto> = emptyList(),

    val termsofuse: String? = null,
    val total: Int? = null,
)
