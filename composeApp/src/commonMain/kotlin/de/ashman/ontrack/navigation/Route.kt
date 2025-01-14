package de.ashman.ontrack.navigation

import de.ashman.ontrack.domain.Media
import kotlinx.serialization.Serializable

@Serializable
sealed class Route {
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
        val mediaType: String,
    ) : Route()

    @Serializable
    data class Detail(
        val media: Media,
    ) : Route()
}
