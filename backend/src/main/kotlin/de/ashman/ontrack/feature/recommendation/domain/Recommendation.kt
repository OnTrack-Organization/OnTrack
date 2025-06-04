package de.ashman.ontrack.feature.recommendation.domain

import de.ashman.ontrack.feature.tracking.domain.Media
import de.ashman.ontrack.feature.user.domain.User
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "recommendations")
data class Recommendation(
    @Id
    val id: UUID = UUID.randomUUID(),

    val message: String? = null,

    @Embedded
    val media: Media,

    @ManyToOne(fetch = FetchType.LAZY)
    val sender: User,

    @ManyToOne(fetch = FetchType.LAZY)
    val receiver: User,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    var createdAt: Instant = Instant.now(),
)
