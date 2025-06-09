package de.ashman.ontrack.feature.user.controller.dto

import de.ashman.ontrack.feature.friend.domain.FriendStatus

data class OtherUserDto(
    val user: UserDto,
    val friendStatus: FriendStatus,
)
