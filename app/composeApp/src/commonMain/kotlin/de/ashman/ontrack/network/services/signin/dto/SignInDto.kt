package de.ashman.ontrack.network.services.signin.dto

import kotlinx.serialization.Serializable

@Serializable
data class SignInDto(
    val fcmToken: String
)