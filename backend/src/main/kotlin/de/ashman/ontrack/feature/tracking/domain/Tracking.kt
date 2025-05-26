package de.ashman.ontrack.feature.tracking.domain

import de.ashman.ontrack.feature.user.domain.User
import jakarta.persistence.*
import java.time.Instant
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
data class Tracking(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    val user: User,

    @Enumerated(EnumType.STRING)
    var status: TrackStatus,

    @Embedded
    val media: Media,

    @Column(name = "updated_at")
    var updatedAt: Instant = Instant.now()
) {
    fun changeStatus(newStatus: TrackStatus) {
        status = newStatus
        updatedAt = Instant.now()
    }
}

