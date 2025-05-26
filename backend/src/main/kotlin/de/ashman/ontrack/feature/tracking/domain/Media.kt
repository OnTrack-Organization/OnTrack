package de.ashman.ontrack.feature.tracking.domain

import de.ashman.ontrack.feature.tracking.controller.dto.MediaDto
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
data class Media(
    @Column(name = "media_id", nullable = false)
    val id: String,

    @Column(name = "media_type", nullable = false)
    @Enumerated(EnumType.STRING)
    val type: MediaType,

    @Column(name = "media_title", nullable = false)
    val title: String,

    @Column(name = "media_cover_url")
    val coverUrl: String?
)

fun MediaDto.toEntity() = Media(
    id = id,
    type = type,
    title = title,
    coverUrl = coverUrl
)
