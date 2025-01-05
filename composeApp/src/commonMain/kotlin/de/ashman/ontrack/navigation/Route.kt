package de.ashman.ontrack.navigation

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
    data class Movie(
        val id: String,
    ) : Route()

    @Serializable
    data class Show(
        val id: String,
    ) : Route()

    @Serializable
    data class Book(
        val id: String,
    ) : Route()

    @Serializable
    data class Videogame(
        val id: String,
    ) : Route()

    @Serializable
    data class Album(
        val id: String,
    ) : Route()

    @Serializable
    data class Boardgame(
        val id: String,
    ) : Route()
}
