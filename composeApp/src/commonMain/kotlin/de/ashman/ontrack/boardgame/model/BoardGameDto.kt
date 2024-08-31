package de.ashman.ontrack.boardgame.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BoardGameDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("yearpublished")
    val yearPublished: String? = null,
    @SerialName("minplayers")
    val minPlayers: Int? = null,
    @SerialName("maxplayers")
    val maxPlayers: Int? = null,
    @SerialName("playingtime")
    val playingTime: Int? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("age")
    val age: Int? = null,
    @SerialName("thumbnail")
    val thumbnail: String? = null,
)