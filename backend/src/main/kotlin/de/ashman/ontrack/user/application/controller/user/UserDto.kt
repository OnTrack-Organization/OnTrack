package de.ashman.ontrack.user.application.controller.user

import de.ashman.ontrack.user.domain.model.User

data class UserDto(
    val id: String,
    val username: String,
    val name: String,
    val profilePictureUrl: String,
)

fun User.toUserDto() = UserDto(
    id,
    username!!,
    name,
    profilePictureUrl
)
