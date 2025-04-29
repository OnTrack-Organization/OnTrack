package de.ashman.ontrack.tracking.application.controller

import de.ashman.ontrack.tracking.domain.model.Media
import de.ashman.ontrack.tracking.domain.model.MediaType

data class MediaDto(
    val id: String,
    val type: MediaType,
    val title: String,
    val coverUrl: String?
)

fun Media.toDto(): MediaDto = MediaDto(
    id = id,
    type = type,
    title = title,
    coverUrl = coverUrl,
)