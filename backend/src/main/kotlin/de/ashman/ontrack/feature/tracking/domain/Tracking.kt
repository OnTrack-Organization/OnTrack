package de.ashman.ontrack.feature.tracking.domain

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(
    name = "trackings",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uniq_media_tracking_per_user",
            columnNames = ["id", "user_id"]
        )
    ]
)
final class Tracking(
    @Column(name = "user_id", nullable = false)
    val userId: String,
    status: TrackStatus,
    @Embedded
    val media: Media
) {
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID()

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: TrackStatus = status
        private set

    fun changeStatus(newStatus: TrackStatus) {
        status = newStatus
    }
}

