package de.ashman.ontrack.media.model

import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.serialization.Serializable

@CommonParcelize
@Serializable
sealed class Media(): CommonParcelable {
    abstract val id: String
    abstract val mediaType: MediaType
    abstract val name: String
    abstract val coverUrl: String
    abstract val releaseYear: String?
    abstract val consumeStatus: ConsumeStatus?
    abstract val userRating: Float?

    abstract fun getMainInfoItems(): List<String>
}
