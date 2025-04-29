package de.ashman.ontrack.tracking.domain.model

import de.ashman.ontrack.tracking.application.controller.MediaDto
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class Media(
    @Column(name = "media_id", nullable = false)
    val id: String,

    @Column(name = "media_type", nullable = false)
    val type: MediaType,

    @Column(name = "title", nullable = false)
    val title: String,

    @Column(name = "cover_url")
    val coverUrl: String?
)

fun MediaDto.toEntity(): Media = Media(
    id = id,
    type = type,
    title = title,
    coverUrl = coverUrl
)