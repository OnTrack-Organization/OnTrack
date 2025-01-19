package de.ashman.ontrack.domain

import de.ashman.ontrack.domain.sub.MediaType
import de.ashman.ontrack.domain.sub.TrackStatus
import de.ashman.ontrack.navigation.CommonParcelable
import de.ashman.ontrack.navigation.CommonParcelize
import kotlinx.serialization.Serializable

@CommonParcelize
@Serializable
sealed class Media(): CommonParcelable {
    abstract val id: String
    abstract val mediaType: MediaType
    abstract val title: String
    abstract val coverUrl: String
    abstract val releaseYear: String?
    abstract val trackStatus: TrackStatus?

    abstract fun getMainInfoItems(): List<String>
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
