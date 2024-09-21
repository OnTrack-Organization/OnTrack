package de.ashman.ontrack.navigation

import kotlinx.serialization.Serializable

@Serializable
object Login

// BOTTOM NAVIGATION
@Serializable
object Home

@Serializable
data object Feed

@Serializable
data object Shelf

@Serializable
data class Movie(
    val id: Int,
)

/*data class MediaDetails(
    val id: String,
)*/

/*@Serializable
data class Detail(
    val id: String,
)*/


