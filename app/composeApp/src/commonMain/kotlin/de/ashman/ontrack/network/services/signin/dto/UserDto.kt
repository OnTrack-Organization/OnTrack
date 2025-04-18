package de.ashman.ontrack.network.services.signin.dto

import de.ashman.ontrack.domain.newdomains.NewUser
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val username: String,
    val name: String,
    val email: String,
    val profilePictureUrl: String,
)

fun UserDto.toDomain() = NewUser(
    id = id,
    name = name,
    username = username,
    email = email,
    profilePictureUrl = profilePictureUrl,
)