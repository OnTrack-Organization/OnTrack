package de.ashman.ontrack.user.application.controller

import de.ashman.ontrack.user.domain.User
import jakarta.validation.constraints.NotBlank

data class UserDto(
    val id: String,
    @NotBlank
    val username: String,
    val name: String,
    val email: String,
    val profilePictureUrl: String
)

fun User.toDto() = UserDto(
    id = id,
    username = username.orEmpty(),
    name = name,
    email = email,
    profilePictureUrl = profilePictureUrl
)

