package de.ashman.ontrack.media.model

import kotlinx.serialization.Serializable

@Serializable
data class BoardGame(
    override val id: String,
    override val type: MediaType = MediaType.BOARDGAME,
    override val name: String,
    override val consumeStatus: ConsumeStatus? = null,
    override val userRating: Float? = null,
    override val coverUrl: String,
    override val releaseYear: String?,
    val minPlayers: String?,
    val maxPlayers: String?,
    val playingTime: String?,
    val description: String?,
    val minAge: String?,
    val thumbnail: String?,
    val ratings: Ratings?,
) : Media() {
    override fun getMainInfoItems(): List<String> {
        val infoItems = mutableListOf<String>()

        releaseYear?.let { infoItems.add(it) }
        playingTime?.let { infoItems.add("$it Min") }
        // TODO change
        if (!minPlayers.isNullOrEmpty() && !maxPlayers.isNullOrEmpty()) {
            infoItems.add("$minPlayers-$maxPlayers Players")
        } else if (!minPlayers.isNullOrEmpty()) {
            infoItems.add("$minPlayers Players")
        } else if (!maxPlayers.isNullOrEmpty()) {
            infoItems.add("$maxPlayers Players")
        }

        return infoItems
    }
}

@Serializable
data class Ratings(
    val usersRated: Int?,
    val average: Double?,
    val numWeights: Int?,
    val averageWeight: Double?,
)