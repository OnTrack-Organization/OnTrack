package de.ashman.ontrack.db.entity.user

import de.ashman.ontrack.domain.user.FriendRequestStatus
import kotlinx.serialization.Serializable

@Serializable
data class FriendRequestEntity(
    val userData: UserData,
    val status: FriendRequestStatus = FriendRequestStatus.PENDING,
)
