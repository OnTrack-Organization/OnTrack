package de.ashman.ontrack.domain.media

import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.serialization.Serializable
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_player_count_range
import ontrack.composeapp.generated.resources.detail_playing_time
import ontrack.composeapp.generated.resources.detail_single_player_count
import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.getString

@Serializable
data class Boardgame(
    override val id: String,
    override val mediaType: MediaType = MediaType.BOARDGAME,
    override val title: String,
    override val coverUrl: String? = null,
    override val releaseYear: String? = null,
    override val description: String? = null,
    override val apiRating: Double? = null,
    override val apiRatingCount: Int? = null,
    val boardgameType: String? = null,
    val minPlayers: Int? = null,
    val maxPlayers: Int? = null,
    val playingTime: Int? = null,
    val weight: Double? = null,
    val weightCount: Int? = null,
    val franchise: List<Boardgame>? = null,
    val designer: BoardgameDesigner? = null,
    val images: List<BoardgameImage>? = null,
) : Media() {
    override suspend fun getMainInfoItems(): List<String> {
        val infoItems = mutableListOf<String>()

        releaseYear?.let { infoItems.add(it) }
        playingTime?.let { infoItems.add(getString(Res.string.detail_playing_time, it)) }

        minPlayers?.let { min ->
            maxPlayers?.let { max ->
                infoItems.add(
                    if (min == max) {
                        getPluralString(Res.plurals.detail_single_player_count, min, min)
                    } else {
                        getString(Res.string.detail_player_count_range, min, max)
                    }
                )
            }
        }

        return infoItems
    }
}

@CommonParcelize
@Serializable
data class BoardgameDesigner(
    val id: String,
    val name: String,
    val imageUrl: String? = null,
    val bio: String? = null,
) : CommonParcelable

@CommonParcelize
@Serializable
data class BoardgameImage(
    val id: String,
    val imageUrl: String,
) : CommonParcelable