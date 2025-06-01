package de.ashman.ontrack.network.services.notification.dto

import de.ashman.ontrack.domain.notification.SimplePost
import de.ashman.ontrack.network.services.tracking.dto.TrackingDto
import de.ashman.ontrack.network.services.tracking.dto.toDomain
import kotlinx.serialization.Serializable

@Serializable
data class SimplePostDto(
    val id: String,
    val tracking: TrackingDto,
)

fun SimplePostDto.toDomain() = SimplePost(
    id = id,
    tracking = tracking.toDomain(),
)