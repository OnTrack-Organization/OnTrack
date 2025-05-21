package de.ashman.ontrack.feature.user.controller.dto

import de.ashman.ontrack.feature.user.domain.User
import jakarta.validation.constraints.NotBlank

data class AccountDto(
    val id: String,
    @NotBlank
    val username: String,
    val name: String,
    val email: String,
    val profilePictureUrl: String
)

fun User.toAccountDto() = AccountDto(
    id = id,
    username = username.orEmpty(),
    name = name,
    email = email,
    profilePictureUrl = profilePictureUrl
)

