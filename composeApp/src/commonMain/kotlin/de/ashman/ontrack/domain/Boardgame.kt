package de.ashman.ontrack.domain

import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.serialization.Serializable

@Serializable
data class Boardgame(
    override val id: String,
    override val mediaType: MediaType = MediaType.BOARDGAME,
    override val name: String,
    override val consumeStatus: ConsumeStatus? = null,
    override val userRating: Float? = null,
    override val coverUrl: String,
    override val releaseYear: String? = null,
    val description: String?,
    val boardgameType: String? = null,
    val minPlayers: String? = null,
    val maxPlayers: String? = null,
    val playingTime: String? = null,
    val minAge: String? = null,
    val thumbnail: String? = null,
    val ratings: Ratings? = null,
    val franchiseItems: List<Boardgame>? = null,
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