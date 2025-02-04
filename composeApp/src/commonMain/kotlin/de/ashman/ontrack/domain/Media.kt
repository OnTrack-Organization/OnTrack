package de.ashman.ontrack.domain

import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.serialization.Serializable

@CommonParcelize
@Serializable
sealed class Media() : CommonParcelable {
    abstract val id: String
    abstract val mediaType: MediaType
    abstract val title: String
    abstract val coverUrl: String?
    abstract val releaseYear: String?
    abstract val description: String?
    abstract val apiRating: Double?
    abstract val apiRatingCount: Int?

    abstract suspend fun getMainInfoItems(): List<String>
}
