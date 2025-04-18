package de.ashman.ontrack.domain.media

import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.serialization.Serializable

@Serializable
data class Videogame(
    override val id: String,
    override val mediaType: MediaType = MediaType.VIDEOGAME,
    override val title: String,
    override val coverUrl: String? = null,
    override val detailUrl: String,
    override val releaseYear: String? = null,
    override val description: String? = null,
    override val apiRating: Double? = null,
    override val apiRatingCount: Int? = null,
    val franchises: List<Franchise>? = null,
    val genres: List<String>? = null,
    val involvedCompanies: List<String>? = null,
    val platforms: List<Platform>? = null,
    val similarGames: List<Videogame>? = null,
    val screenshots: List<String>? = null,
) : Media() {
    override suspend fun getMainInfoItems(): List<String> {
        val infoItems = mutableListOf<String>()

        releaseYear?.let { infoItems.add(it) }
        involvedCompanies?.let { infoItems.add(it.first()) }

        return infoItems
    }

    fun getPlatformNames(): List<String>? {
        return this.platforms?.map { it.name }
    }
}

@CommonParcelize
@Serializable
data class Franchise(
    val id: Int,
    val name: String,
    val games: List<Videogame>,
    val imageUrl: String?
) : CommonParcelable

@CommonParcelize
@Serializable
data class Platform(
    val id: Int,
    val abbreviation: String?,
    val name: String,
    val platformLogo: String?,
) : CommonParcelable
