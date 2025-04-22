package de.ashman.ontrack.domain.media

import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import de.ashman.ontrack.network.services.tracking.dto.MediaDataDto
import kotlinx.serialization.Serializable

@CommonParcelize
@Serializable
sealed class Media() : CommonParcelable {
    abstract val id: String
    abstract val mediaType: MediaType
    abstract val title: String
    abstract val coverUrl: String?
    abstract val detailUrl: String
    abstract val releaseYear: String?
    abstract val description: String?
    abstract val apiRating: Double?
    abstract val apiRatingCount: Int?

    abstract suspend fun getMainInfoItems(): List<String>
}

fun Media.toDto() = MediaDataDto(
    id = id,
    type = mediaType,
    title = title,
    coverUrl = coverUrl,
)