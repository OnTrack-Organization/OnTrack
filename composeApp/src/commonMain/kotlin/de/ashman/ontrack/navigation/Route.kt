package de.ashman.ontrack.navigation

import de.ashman.ontrack.media.model.MediaType
import de.ashman.ontrack.navigation.BottomNavItem.FeedNav
import de.ashman.ontrack.navigation.BottomNavItem.SearchNav
import de.ashman.ontrack.navigation.BottomNavItem.ShelfNav
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

    // TODO change to just one in the future, but lets make it work for now
    @Serializable
    data class Detail(
        val id: String,
        val mediaType: MediaType,
    ) : Route()
}
