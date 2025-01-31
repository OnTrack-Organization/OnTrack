package de.ashman.ontrack.domain

import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.serialization.Serializable
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_runtime
import org.jetbrains.compose.resources.getString

@Serializable
data class Movie(
    override val id: String,
    override val mediaType: MediaType = MediaType.MOVIE,
    override val title: String,
    override val coverUrl: String,
    override val releaseYear: String? = null,
    override val trackStatus: TrackStatus? = null,
    override val description: String? = null,
    override val ratingStats: RatingStats? = null,
    val collection: MovieCollection? = null,
    val genres: List<String>? = null,
    val popularity: Double? = null,
    val runtime: Int? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
    val similarMovies: List<Movie>? = null,
    val director: Director? = null,
) : Media() {
    override suspend fun getMainInfoItems(): List<String> {
        val infoItems = mutableListOf<String>()

        releaseYear?.let { infoItems.add(it) }
        runtime?.let { infoItems.add(getString(Res.string.detail_runtime, it)) }

        return infoItems
    }
}

@CommonParcelize
@Serializable
data class MovieCollection(
    val id: Int? = null,
    val name: String? = null,
    val movies: List<Movie>? = null,
    val imageUrl: String? = null,
): CommonParcelable

@CommonParcelize
@Serializable
data class Director(
    val id: String,
    val name: String?,
    val imageUrl: String?,
    val birthDate: String?,
    val deathDate: String?,
    val bio: String? = null,
) : CommonParcelable
