package de.ashman.ontrack.media.domain

import kotlinx.serialization.Serializable

@Serializable
data class BoardGame(
    override val id: String,
    override val type: MediaType = MediaType.BOARDGAME,
    override val name: String,
    override val consumeStatus: ConsumeStatus? = ConsumeStatus.CATALOG,
    override val userRating: Float = 0F,
    override val coverUrl: String,
    val yearPublished: String?,
    val minPlayers: String?,
    val maxPlayers: String?,
    val playingTime: String?,
    val description: String?,
    val minAge: String?,
    val thumbnail: String?,
    val ratings: Ratings?,
) : Media()

@Serializable
data class Ratings(
    val usersRated: Int?,
    val average: Double?,
    val numWeights: Int?,
    val averageWeight: Double?,
)