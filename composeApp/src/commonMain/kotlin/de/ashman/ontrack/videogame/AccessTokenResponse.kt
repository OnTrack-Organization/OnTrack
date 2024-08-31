package de.ashman.ontrack.videogame

import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenResponse(
    val accessToken: String,
    val expiresIn: Long,
    val tokenType: String
)