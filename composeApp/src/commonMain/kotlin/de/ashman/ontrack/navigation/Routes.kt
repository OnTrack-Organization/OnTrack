package de.ashman.ontrack.navigation

import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object Home

@Serializable
data class Movie(
    val id: Int,
)

/*data class MediaDetails(
    val id: String,
)*/

@Serializable
data object Feed

/*@Serializable
data class Detail(
    val id: String,
)*/


