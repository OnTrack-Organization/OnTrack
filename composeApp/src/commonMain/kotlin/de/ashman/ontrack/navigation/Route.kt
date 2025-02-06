package de.ashman.ontrack.navigation

import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.MediaType
import kotlinx.serialization.Serializable

@Serializable
sealed class Route {
    // INIT
    @Serializable
    data object Start : Route()

    @Serializable
    data object Intro : Route()

    @Serializable
    data object Login : Route()

    // BOTTOM NAVIGATION
    @Serializable
    data object Feed : Route()

    @Serializable
    data object Search : Route()

    @Serializable
    data object Shelf : Route()

    // OTHER
    @Serializable
    data class ShelfList(
        val mediaType: MediaType,
    ) : Route()

    @Serializable
    data class Detail(
        val media: Media,
    ) : Route()

    @Serializable
    data class OtherShelf(
        val userId: String,
    ) : Route()
}
