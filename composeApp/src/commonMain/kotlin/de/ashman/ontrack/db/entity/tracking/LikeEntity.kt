package de.ashman.ontrack.db.entity.tracking

import de.ashman.ontrack.db.entity.user.UserData
import de.ashman.ontrack.domain.tracking.Like
import kotlinx.serialization.Serializable

@Serializable
data class LikeEntity(
    val id: String,
    val userData: UserData,
    val timestamp: Long,
) {
    fun toDomain() = Like(
        id = id,
        userData = userData,
        timestamp = timestamp,
    )
}