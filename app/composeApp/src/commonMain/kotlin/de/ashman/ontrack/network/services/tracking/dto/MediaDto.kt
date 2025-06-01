package de.ashman.ontrack.network.services.tracking.dto

import de.ashman.ontrack.domain.media.MediaData
import de.ashman.ontrack.domain.media.MediaType
import kotlinx.serialization.Serializable

@Serializable
data class MediaDto(
    val id: String,
    val type: MediaType,
    val title: String,
    val coverUrl: String?,
)

fun MediaDto.toDomain() = MediaData(
    id = id,
    type = type,
    title = title,
    coverUrl = coverUrl,
)