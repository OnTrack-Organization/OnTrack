package de.ashman.ontrack.tracking.domain.model

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
