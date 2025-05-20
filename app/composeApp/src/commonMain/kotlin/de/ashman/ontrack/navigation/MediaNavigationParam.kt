package de.ashman.ontrack.navigation

import de.ashman.ontrack.domain.media.MediaType
import kotlinx.serialization.Serializable

@Serializable
data class MediaNavigationParam(
    val id: String,
    val title: String,
    val coverUrl: String?,
    val type: MediaType,
)