package de.ashman.ontrack.feature.recommendation.domain

import de.ashman.ontrack.feature.tracking.domain.Media
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "recommendations")
final class Recommendation(
    @Column(name = "sender_id", nullable = false)
    val senderId: String,
    @Column(name = "receiver_id", nullable = false)
    val receiverId: String,
    @Embedded
    val media: Media,
    @Column(name = "message")
    val message: String?,
) {
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID()

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
}