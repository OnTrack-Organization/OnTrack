package de.ashman.ontrack.domain

import de.ashman.ontrack.domain.sub.MediaType
import de.ashman.ontrack.domain.sub.TrackStatus
import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.serialization.Serializable

@Serializable
data class Show(
    override val id: String,
    override val mediaType: MediaType = MediaType.SHOW,
    override val name: String,
    override val coverUrl: String,
    override val releaseYear: String?,
    override val trackStatus: TrackStatus? = null,
    val description: String?,
    val backdropPath: String?,
    val episodeRunTime: List<Int>?,
    val genres: List<String>?,
    val languages: List<String>?,
    val numberOfEpisodes: Int?,
    val numberOfSeasons: Int?,
    val originCountry: List<String>?,
    val originalLanguage: String?,
    val originalName: String?,
    val popularity: Double?,
    val seasons: List<Season>?,
    val status: String?,
    val voteAverage: Double?,
    val voteCount: Int?,
    val similarShows: List<Show>? = null,
) : Media() {
    override fun getMainInfoItems(): List<String> {
        val infoItems = mutableListOf<String>()

        releaseYear?.let { infoItems.add(it) }
        numberOfSeasons?.let { infoItems.add("$it Seasons") }
        numberOfEpisodes?.let { infoItems.add("$it Episodes") }

        return infoItems
    }
}

@CommonParcelize
@Serializable
data class Season(
    val id: Int,
    val airDate: String?,
    val episodeCount: Int?,
    val name: String?,
    val overview: String?,
    val posterPath: String?,
    val seasonNumber: Int?,
    val voteAverage: Double?
) : CommonParcelable
