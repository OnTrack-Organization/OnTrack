package de.ashman.ontrack.network.services.friend.dto

import de.ashman.ontrack.domain.newdomains.UserProfile
import de.ashman.ontrack.network.services.tracking.dto.TrackingDto
import de.ashman.ontrack.network.services.tracking.dto.toDomain

data class UserProfileDto(
    val user: OtherUserDto,
    val trackings: List<TrackingDto>,
)

fun UserProfileDto.toDomain() = UserProfile(
    user = user.toDomain(),
    trackings = trackings.map { it.toDomain() },
)