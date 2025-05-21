package de.ashman.ontrack.tracking.controller.dto

import de.ashman.ontrack.tracking.domain.Media
import de.ashman.ontrack.tracking.domain.MediaType

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