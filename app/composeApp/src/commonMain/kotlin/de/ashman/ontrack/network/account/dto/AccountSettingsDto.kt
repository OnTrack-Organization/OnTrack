package de.ashman.ontrack.network.account.dto

import kotlinx.serialization.Serializable

@Serializable
data class AccountSettingsDto(
    val name: String,
    val username: String,
)