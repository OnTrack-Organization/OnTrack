package de.ashman.ontrack.db.entity.user

import de.ashman.ontrack.domain.user.Friend
import kotlinx.serialization.Serializable

@Serializable
data class FriendEntity(
    val userData: UserData
) {
    fun toDomain(): Friend = Friend(
        userData = userData
    )
}