package de.ashman.ontrack.network.services.friend.dto

import de.ashman.ontrack.domain.newdomains.FriendStatus
import de.ashman.ontrack.domain.newdomains.OtherUser
import de.ashman.ontrack.network.services.signin.dto.UserDto
import de.ashman.ontrack.network.services.signin.dto.toDomain

data class OtherUserDto(
    val user: UserDto,
    val friendStatus: FriendStatus,
)

fun OtherUserDto.toDomain() = OtherUser(
    user = user.toDomain(),
    friendStatus = friendStatus,
)
