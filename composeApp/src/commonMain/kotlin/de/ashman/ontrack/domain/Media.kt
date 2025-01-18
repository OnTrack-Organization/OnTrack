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
    abstract val name: String
    abstract val coverUrl: String
    abstract val releaseYear: String?
    //abstract val trackStatus: TrackStatus?
    // TODO maybe add the TrackStatus here so that it is accessible everywhere in the app

    abstract fun getMainInfoItems(): List<String>
}
