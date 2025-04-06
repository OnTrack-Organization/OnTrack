package de.ashman.ontrack.network.signin.dto

import kotlinx.serialization.Serializable

@Serializable
data class SignInDto(
    val fcmToken: String
)