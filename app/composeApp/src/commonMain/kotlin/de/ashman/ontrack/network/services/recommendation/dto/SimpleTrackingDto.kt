package de.ashman.ontrack.network.services.recommendation.dto

import de.ashman.ontrack.domain.recommendation.SimpleTracking
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.network.services.review.dto.ReviewDto
import de.ashman.ontrack.network.services.review.dto.toDomain
import de.ashman.ontrack.network.services.signin.dto.UserDto
import de.ashman.ontrack.network.services.signin.dto.toDomain
import kotlinx.serialization.Serializable

@Serializable
data class SimpleTrackingDto(
    val user: UserDto,
    val status: TrackStatus,
    val review: ReviewDto?,
    val timestamp: Long,
)

fun SimpleTrackingDto.toDomain() = SimpleTracking(
    user = user.toDomain(),
    status = status,
    review = review?.toDomain(),
    timestamp = timestamp,
)
