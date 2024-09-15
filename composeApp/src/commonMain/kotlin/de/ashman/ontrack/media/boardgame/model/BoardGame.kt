package de.ashman.ontrack.media.boardgame.model

data class BoardGame(
    val id: String,
    val name: String,
    val yearPublished: String? = null,
    val minPlayers: Int? = null,
    val maxPlayers: Int? = null,
    val playingTime: Int? = null,
    val description: String? = null,
    val minAge: Int? = null,
    val thumbnail: String? = null,
    val image: String? = null
)
