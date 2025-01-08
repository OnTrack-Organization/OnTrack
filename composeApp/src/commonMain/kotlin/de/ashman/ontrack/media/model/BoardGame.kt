package de.ashman.ontrack.media.model

import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.serialization.Serializable

@Serializable
data class BoardGame(
    override val id: String,
    override val mediaType: MediaType = MediaType.BOARDGAME,
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
        playingTime?.takeIf { it != "0" }?.let { infoItems.add("$it Min") }

        // Add player count, showing just one value if min and max are the same, excluding 0
        if (!minPlayers.isNullOrEmpty() && minPlayers != "0") {
            infoItems.add(if (minPlayers == maxPlayers) "$minPlayers Players" else "$minPlayers-$maxPlayers Players")
        } else if (!maxPlayers.isNullOrEmpty() && maxPlayers != "0") {
            infoItems.add("$maxPlayers Players")
        }

        return infoItems
    }
}

@CommonParcelize
@Serializable
data class Ratings(
    val usersRated: Int?,
    val average: Double?,
    val numWeights: Int?,
    val averageWeight: Double?,
) : CommonParcelable