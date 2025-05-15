package de.ashman.ontrack.user.application.controller

import de.ashman.ontrack.user.application.controller.user.UserDto

data class OtherUserDto(
    val user: UserDto,
    val friendShipStatus: FriendShipStatus
)
