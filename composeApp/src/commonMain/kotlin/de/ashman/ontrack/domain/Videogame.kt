package de.ashman.ontrack.domain

import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.serialization.Serializable

@Serializable
data class Videogame(
    override val id: String,
    override val mediaType: MediaType = MediaType.VIDEOGAME,
    override val title: String,
    override val coverUrl: String,
    override val releaseYear: String?,
    override val trackStatus: TrackStatus? = null,
    override val description: String?,
    val franchises: List<Franchise>?,
    val genres: List<String>?,
    val involvedCompanies: List<String>?,
    val platforms: List<Platform>?,
    val totalRating: Double?,
    val totalRatingCount: Int?,
    val similarGames: List<Videogame>?,
) : Media() {
    override suspend fun getMainInfoItems(): List<String> {
        val infoItems = mutableListOf<String>()

        releaseYear?.let { infoItems.add(it) }
        involvedCompanies?.let { infoItems.add(it.first()) }

        return infoItems
    }
}

@CommonParcelize
@Serializable
data class Franchise(
    val id: Int,
    val name: String,
) : CommonParcelable

@CommonParcelize
@Serializable
data class Platform(
    val id: Int,
    val abbreviation: String?,
    val name: String,
    val platformLogo: String?,
) : CommonParcelable
