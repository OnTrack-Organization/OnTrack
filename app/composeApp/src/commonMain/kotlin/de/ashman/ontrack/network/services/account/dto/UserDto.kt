package de.ashman.ontrack.network.services.account.dto

import de.ashman.ontrack.domain.user.User
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val username: String,
    val name: String,
    val email: String? = null,
    val profilePictureUrl: String,
)

fun UserDto.toDomain() = User(
    id = id,
    name = name,
    username = username,
    // TODO probably different way
    email = email.orEmpty(),
    profilePictureUrl = profilePictureUrl,
)