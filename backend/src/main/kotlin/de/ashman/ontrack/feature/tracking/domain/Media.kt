package de.ashman.ontrack.feature.tracking.domain

import de.ashman.ontrack.feature.tracking.controller.dto.MediaDto
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
data class Media(
    @Column(name = "media_id")
    val id: String? = null,

    @Column(name = "media_type")
    @Enumerated(EnumType.STRING)
    val type: MediaType? = null,

    @Column(name = "media_title")
    val title: String? = null,

    @Column(name = "media_cover_url")
    val coverUrl: String? = null,
)

fun MediaDto.toEntity() = Media(
    id = id,
    type = type,
    title = title,
    coverUrl = coverUrl
)
