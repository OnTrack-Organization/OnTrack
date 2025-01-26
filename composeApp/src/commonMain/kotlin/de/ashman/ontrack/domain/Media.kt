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
    abstract val coverUrl: String
    abstract val releaseYear: String?
    abstract val description: String?
    abstract val trackStatus: TrackStatus?

    abstract suspend fun getMainInfoItems(): List<String>
}

fun Media.addTrackStatus(trackStatus: TrackStatus?): Media {
    return when (this) {
        is Movie -> this.copy(trackStatus = trackStatus)
        is Show -> this.copy(trackStatus = trackStatus)
        is Book -> this.copy(trackStatus = trackStatus)
        is Videogame -> this.copy(trackStatus = trackStatus)
        is Boardgame -> this.copy(trackStatus = trackStatus)
        is Album -> this.copy(trackStatus = trackStatus)
    }
}

fun Media.removeTrackStatus(): Media {
    return when (this) {
        is Movie -> this.copy(trackStatus = null)
        is Show -> this.copy(trackStatus = null)
        is Book -> this.copy(trackStatus = null)
        is Videogame -> this.copy(trackStatus = null)
        is Boardgame -> this.copy(trackStatus = null)
        is Album -> this.copy(trackStatus = null)
    }
}
