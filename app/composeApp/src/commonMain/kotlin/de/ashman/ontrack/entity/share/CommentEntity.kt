package de.ashman.ontrack.entity.share

import kotlinx.serialization.Serializable

@Serializable
data class CommentEntity(
    val id: String,
    val userId: String,
    val userImageUrl: String,
    val name: String,
    val username: String,
    val comment: String,
    val timestamp: Long,
) {
    // Needed for Ios...
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "userId" to userId,
            "userImageUrl" to userImageUrl,
            "username" to username,
            "name" to name,
            "comment" to comment,
            "timestamp" to timestamp,
        )
    }
}