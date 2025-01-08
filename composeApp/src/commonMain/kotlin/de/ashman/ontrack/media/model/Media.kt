package de.ashman.ontrack.media.model

import kotlinx.serialization.Serializable

@Serializable
sealed class Media {
    abstract val id: String
    abstract val type: MediaType
    abstract val name: String
    abstract val coverUrl: String
    abstract val consumeStatus: ConsumeStatus?
    abstract val userRating: Float?

    abstract fun getMainInfoItems(): List<String>
}
