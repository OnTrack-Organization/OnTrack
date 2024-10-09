package de.ashman.ontrack.media.boardgame.model.entity

import de.ashman.ontrack.media.MediaEntity
import de.ashman.ontrack.media.MediaType
import kotlinx.serialization.Serializable

@Serializable
data class BoardGameEntity(
    override val id: String,
    override val type: MediaType = MediaType.BOARDGAME,
    override val name: String,
    override val coverUrl: String,
    val yearPublished: String?,
    val minPlayers: String?,
    val maxPlayers: String?,
    val playingTime: String?,
    val description: String?,
    val minAge: String?,
    val thumbnail: String?,
    val ratings: RatingsEntity?,
) : MediaEntity

@Serializable
data class RatingsEntity(
    val usersRated: Int?,
    val average: Double?,
    val numWeights: Int?,
    val averageWeight: Double?,
)