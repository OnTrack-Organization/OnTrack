package de.ashman.ontrack.auth

import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenResponse(
    val accessToken: String,
    val expiresIn: Long,
    val tokenType: String
)