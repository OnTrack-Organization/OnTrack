package de.ashman.ontrack.navigation

import de.ashman.ontrack.shelf.MediaType
import kotlinx.serialization.Serializable

@Serializable
sealed class Route {
    @Serializable
    data object Login : Route()

    // BOTTOM NAVIGATION
    @Serializable
    data object Home : Route()

    @Serializable
    data object Feed : Route()

    @Serializable
    data object Shelf : Route()

    @Serializable
    data class ShelfList(
        val mediaType: MediaType,
    ) : Route()

    @Serializable
    data class Movie(
        val id: Int,
    ) : Route()
}
