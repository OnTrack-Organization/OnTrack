package de.ashman.ontrack.network.services.account.dto

import kotlinx.serialization.Serializable

@Serializable
data class SignInDto(
    val fcmToken: String
)