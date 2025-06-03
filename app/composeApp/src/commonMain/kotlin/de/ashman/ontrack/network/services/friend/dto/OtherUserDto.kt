package de.ashman.ontrack.network.services.friend.dto

import de.ashman.ontrack.domain.user.FriendStatus
import de.ashman.ontrack.domain.user.OtherUser
import de.ashman.ontrack.network.services.account.dto.UserDto
import de.ashman.ontrack.network.services.account.dto.toDomain
import kotlinx.serialization.Serializable

@Serializable
data class OtherUserDto(
    val user: UserDto,
    val friendStatus: FriendStatus,
)

fun OtherUserDto.toDomain() = OtherUser(
    user = user.toDomain(),
    friendStatus = friendStatus,
)
