package de.ashman.ontrack.api.auth

import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenResponse(
    val accessToken: String,
    val expiresIn: Long,
    val tokenType: String
)