package de.ashman.ontrack.navigation

import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.newdomains.NewUser
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
        val user: NewUser,
    ) : Route()

    // BOTTOM NAVIGATION
    @Serializable
    data object Share : Route()

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
        val mediaNav: MediaNavigationParam,
    ) : Route()

    @Serializable
    data class OtherShelf(
        val userId: String,
    ) : Route()

    @Serializable
    data object Settings : Route()

    @Serializable
    data object Notifications : Route()

    @Serializable
    data class ShareDetail(
        val trackingId: String,
    ) : Route()
}
