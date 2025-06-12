package de.ashman.ontrack.network.services.account.dto

import kotlinx.serialization.Serializable

@Serializable
data class AccountSettingsResponseDto(
    val user: UserDto? = null,
    val error: String? = null
)
