package de.ashman.ontrack.feature.user.controller.dto

import de.ashman.ontrack.feature.user.domain.User

data class UserDto(
    val id: String,
    val username: String,
    val name: String,
    val profilePictureUrl: String,
)

fun User.toDto() = UserDto(
    id,
    username!!,
    name,
    profilePictureUrl
)
