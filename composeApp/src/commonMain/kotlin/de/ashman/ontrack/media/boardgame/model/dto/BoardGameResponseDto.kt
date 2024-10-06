package de.ashman.ontrack.media.boardgame.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BoardGameResponseDto(
    @SerialName("items")
    val boardGames: List<BoardGameDto> = emptyList(),

    val termsofuse: String? = null,
    val total: Int? = null,
)
