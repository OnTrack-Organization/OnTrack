package de.ashman.ontrack.db.entity.tracking

import de.ashman.ontrack.db.entity.user.UserData
import de.ashman.ontrack.domain.tracking.Comment
import kotlinx.serialization.Serializable

@Serializable
data class CommentEntity(
    val id: String,
    val userData: UserData,
    val comment: String,
    val timestamp: Long,
) {
    fun toDomain() = Comment(
        id = id,
        userData = userData,
        comment = comment,
    )
}