package de.ashman.ontrack.feature.recommendation.controller.dto

import de.ashman.ontrack.feature.review.controller.dto.ReviewDto
import de.ashman.ontrack.feature.tracking.domain.TrackStatus
import de.ashman.ontrack.feature.tracking.domain.Tracking
import de.ashman.ontrack.feature.user.controller.dto.UserDto
import java.time.ZoneOffset

data class SimpleTrackingDto(
    val user: UserDto,
    val status: TrackStatus,
    val review: ReviewDto?,
    val timestamp: Long,
)

fun Tracking.toSimpleDto(userDto: UserDto, reviewDto: ReviewDto?) = SimpleTrackingDto(
    user = userDto,
    status = status,
    review = reviewDto,
    timestamp = createdAt.toInstant(ZoneOffset.UTC).toEpochMilli(),
)