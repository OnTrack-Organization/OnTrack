package de.ashman.ontrack.feature.notification.controller.dto

import de.ashman.ontrack.feature.share.domain.Post
import de.ashman.ontrack.feature.tracking.controller.dto.TrackingDto
import de.ashman.ontrack.feature.tracking.controller.dto.toDto
import java.util.*

data class SimplePostDto(
    val id: UUID,
    val tracking: TrackingDto,
)

fun Post.toSimpleDto() = SimplePostDto(
    id = id,
    tracking = tracking.toDto()
)