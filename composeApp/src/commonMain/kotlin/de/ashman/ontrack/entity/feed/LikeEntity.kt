package de.ashman.ontrack.entity.feed

import kotlinx.serialization.Serializable

@Serializable
data class LikeEntity(
    val userId: String,
    val name: String,
    val username: String,
    val userImageUrl: String,
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "name" to name,
            "username" to username,
            "userImageUrl" to userImageUrl,
        )
    }
}