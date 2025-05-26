package de.ashman.ontrack.feature.recommendation.domain

import de.ashman.ontrack.feature.tracking.domain.Media
import de.ashman.ontrack.feature.user.domain.User
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "recommendations")
data class Recommendation(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    val sender: User,
    @ManyToOne(fetch = FetchType.LAZY)
    val receiver: User,

    @Embedded
    val media: Media,

    val message: String? = null,

    @Column(name = "created_at")
    val createdAt: Instant = Instant.now(),
)
