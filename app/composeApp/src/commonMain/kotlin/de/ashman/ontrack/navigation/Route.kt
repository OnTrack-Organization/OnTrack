package de.ashman.ontrack.navigation

import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.user.User
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

    @Serializable
    data class Setup(
        val user: User?,
    ) : Route()

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
        val userId: String,
        val mediaType: MediaType,
    ) : Route()

    @Serializable
    data class Detail(
        val mediaNavItems: MediaNavigationItems,
    ) : Route()

    @Serializable
    data class OtherShelf(
        val userId: String,
    ) : Route()

    @Serializable
    data object Settings : Route()

    @Serializable
    data object Notifications : Route()
}
