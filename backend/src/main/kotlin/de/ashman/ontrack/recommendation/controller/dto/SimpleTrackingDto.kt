package de.ashman.ontrack.recommendation.controller.dto

import de.ashman.ontrack.tracking.domain.model.TrackStatus
import de.ashman.ontrack.tracking.domain.model.Tracking
import de.ashman.ontrack.user.application.controller.user.UserDto
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