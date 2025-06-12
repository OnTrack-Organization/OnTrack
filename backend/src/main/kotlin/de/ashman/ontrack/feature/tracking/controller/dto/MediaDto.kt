package de.ashman.ontrack.feature.tracking.controller.dto

import de.ashman.ontrack.feature.tracking.domain.Media
import de.ashman.ontrack.feature.tracking.domain.MediaType

data class MediaDto(
    val id: String,
    val type: MediaType,
    val title: String,
    val coverUrl: String?
)

fun Media.toDto(): MediaDto = MediaDto(
    id = id!!,
    type = type!!,
    title = title!!,
    coverUrl = coverUrl,
)