package de.ashman.ontrack.feature.recommendation.controller.dto

import de.ashman.ontrack.feature.tracking.domain.TrackStatus
import de.ashman.ontrack.feature.tracking.domain.Tracking
import de.ashman.ontrack.feature.user.controller.dto.UserDto
import java.time.ZoneOffset

data class SimpleTrackingDto(
    val user: UserDto,
    val status: TrackStatus,
    val timestamp: Long,
)

fun Tracking.toSimpleDto(userDto: UserDto) = SimpleTrackingDto(
    user = userDto,
    status = status,
    timestamp = createdAt.toInstant(ZoneOffset.UTC).toEpochMilli(),
)