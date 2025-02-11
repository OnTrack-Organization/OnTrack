package de.ashman.ontrack.navigation

import de.ashman.ontrack.domain.MediaType
import kotlinx.serialization.Serializable

@Serializable
data class MediaNavigationItems(
    val id: String,
    val title: String?,
    val coverUrl: String?,
    val mediaType: MediaType,
)