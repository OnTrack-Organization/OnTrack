package de.ashman.ontrack.network.services.friend.dto

import de.ashman.ontrack.domain.user.UserProfile
import de.ashman.ontrack.network.services.tracking.dto.TrackingDto
import de.ashman.ontrack.network.services.tracking.dto.toDomain
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val user: OtherUserDto,
    val trackings: List<TrackingDto>,
    val blocked: Boolean,
)

fun UserProfileDto.toDomain() = UserProfile(
    user = user.toDomain(),
    trackings = trackings.map { it.toDomain() },
    blocked = blocked,
)