package de.ashman.ontrack.domain

import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.serialization.Serializable
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_episodes
import ontrack.composeapp.generated.resources.detail_seasons
import org.jetbrains.compose.resources.getPluralString

@Serializable
data class Show(
    override val id: String,
    override val mediaType: MediaType = MediaType.SHOW,
    override val title: String,
    override val coverUrl: String,
    override val releaseYear: String? = null,
    override val trackStatus: TrackStatus? = null,
    override val description: String? = null,
    val backdropPath: String? = null,
    val episodeRunTime: List<Int>? = null,
    val genres: List<String>? = null,
    val languages: List<String>? = null,
    val numberOfEpisodes: Int? = null,
    val numberOfSeasons: Int? = null,
    val originCountry: List<String>? = null,
    val originalLanguage: String? = null,
    val originalName: String? = null,
    val popularity: Double? = null,
    val seasons: List<Season>? = null,
    val status: String? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
    val similarShows: List<Show>? = null,
    val seasonNumber: Int? = null,
    val director: Director? = null,
) : Media() {
    override suspend fun getMainInfoItems(): List<String> {
        val infoItems = mutableListOf<String>()

        releaseYear?.let { infoItems.add(it) }
        numberOfSeasons?.let { infoItems.add(getPluralString(Res.plurals.detail_seasons, it, it)) }
        numberOfEpisodes?.let { infoItems.add(getPluralString(Res.plurals.detail_episodes, it, it)) }

        return infoItems
    }
}

@CommonParcelize
@Serializable
data class Season(
    val id: Int,
    val title: String?,
    val coverUrl: String?,
    val description: String?,
    val releaseYear: String?,
    val seasonNumber: Int?,
    val episodeCount: Int?,
    val voteAverage: Double?
) : CommonParcelable
