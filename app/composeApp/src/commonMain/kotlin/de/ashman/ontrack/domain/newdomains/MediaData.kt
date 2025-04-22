package de.ashman.ontrack.domain.newdomains

import de.ashman.ontrack.domain.media.MediaType

data class MediaData(
    val id: String,
    val type: MediaType,
    val title: String,
    val coverUrl: String?,
)
