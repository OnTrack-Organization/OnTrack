package de.ashman.ontrack.user

import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.db.entity.MediaEntity
import kotlinx.serialization.Serializable

data class User(
    val id: String = "",
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val friends: List<String> = emptyList(),
    val media: List<Media> = emptyList(),
)

@Serializable
data class UserEntity(
    val id: String,
    val name: String,
    val username: String,
    val email: String,
    val friends: List<String>,
    val media: List<MediaEntity>,
)