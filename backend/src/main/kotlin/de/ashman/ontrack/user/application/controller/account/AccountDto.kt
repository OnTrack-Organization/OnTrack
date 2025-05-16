package de.ashman.ontrack.user.application.controller.account

import de.ashman.ontrack.user.domain.model.User
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

