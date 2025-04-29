package de.ashman.ontrack.network.services.tracking.dto

import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.newdomains.MediaData
import kotlinx.serialization.Serializable

@Serializable
data class MediaDataDto(
    val id: String,
    val type: MediaType,
    val title: String,
    val coverUrl: String?,
)

fun MediaDataDto.toDomain() = MediaData(
    id = id,
    type = type,
    title = title,
    coverUrl = coverUrl,
)