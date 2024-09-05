package de.ashman.ontrack.boardgame.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BoardGamesResponse(
    @SerialName("termsofuse")
    val termsOfUse: String? = null,

    @SerialName("items")
    val boardGames: List<BoardGameDto>? = null
)
