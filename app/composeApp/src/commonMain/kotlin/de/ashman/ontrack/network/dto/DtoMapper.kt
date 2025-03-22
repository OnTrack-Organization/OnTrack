package de.ashman.ontrack.network.dto

import de.ashman.ontrack.domain.user.User

fun UserDto.toDomain() = User(
    id = id,
    fcmToken = fcmToken,
    email = email,
    name = name,
    username = username,
    imageUrl = imageUrl,
)

fun User.toDto() = UserDto(
    id = id,
    fcmToken = fcmToken,
    email = email,
    name = name,
    username = username,
    imageUrl = imageUrl,
    // TODO updatedat im be setzen
    updatedAt = "",
)