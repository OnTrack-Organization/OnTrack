package de.ashman.ontrack.domain.user

import de.ashman.ontrack.db.entity.user.FriendEntity
import de.ashman.ontrack.db.entity.user.FriendRequestEntity
import de.ashman.ontrack.db.entity.user.UserData
import kotlinx.serialization.Serializable

@Serializable
data class FriendRequest(
    val userData: UserData,
    val status: FriendRequestStatus = FriendRequestStatus.PENDING,
) {
    fun toEntity(): FriendRequestEntity = FriendRequestEntity(
        userData = userData,
        status = status,
    )

    fun toFriendEntity(): FriendEntity = FriendEntity(
        userData = userData,
    )
}

@Serializable
enum class FriendRequestStatus {
    PENDING, ACCEPTED, DECLINED, CANCELLED
}