package de.ashman.ontrack.domain.notification

import de.ashman.ontrack.domain.media.MediaData

data class SimpleRecommendation(
    val media: MediaData,
    val message: String?,
)