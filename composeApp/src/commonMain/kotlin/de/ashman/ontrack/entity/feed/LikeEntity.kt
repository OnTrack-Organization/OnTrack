package de.ashman.ontrack.entity.feed

import kotlinx.serialization.Serializable

@Serializable
data class LikeEntity(
    val userId: String,
    val username: String,
    val userImageUrl: String,
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "username" to username,
            "userImageUrl" to userImageUrl,
        )
    }
}