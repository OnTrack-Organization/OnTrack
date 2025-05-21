package de.ashman.ontrack.feature.user.controller.dto

import de.ashman.ontrack.feature.tracking.controller.dto.TrackingDto

data class UserProfileDto(
    val user: OtherUserDto,
    val trackings: List<TrackingDto>
)
