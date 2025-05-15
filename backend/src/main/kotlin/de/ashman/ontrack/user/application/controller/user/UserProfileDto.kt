package de.ashman.ontrack.user.application.controller.user

import de.ashman.ontrack.tracking.application.controller.TrackingDto
import de.ashman.ontrack.user.application.controller.OtherUserDto

data class UserProfileDto(
    val user: OtherUserDto,
    val trackings: List<TrackingDto>
)
