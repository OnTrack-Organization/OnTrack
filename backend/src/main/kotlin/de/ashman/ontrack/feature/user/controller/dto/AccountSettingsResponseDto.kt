package de.ashman.ontrack.feature.user.controller.dto

data class AccountSettingsResponseDto(
    val user: UserDto? = null,
    val error: String? = null
)
