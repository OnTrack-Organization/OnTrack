package de.ashman.ontrack.media.boardgame.model.domain

data class BoardGame(
    val id: String,
    val name: String,
    val yearPublished: String?,
    val minPlayers: String?,
    val maxPlayers: String?,
    val playingTime: String?,
    val description: String?,
    val minAge: String?,
    val thumbnail: String?,
    val image: String?,
    val ratings: Ratings?,
)

data class Ratings(
    val usersRated: Int?,
    val average: Double?,
    val numWeights: Int?,
    val averageWeight: Double?,
)