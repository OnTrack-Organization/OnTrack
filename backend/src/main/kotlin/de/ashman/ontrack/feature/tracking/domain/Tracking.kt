package de.ashman.ontrack.feature.tracking.domain

import de.ashman.ontrack.feature.review.domain.Review
import de.ashman.ontrack.feature.share.domain.Post
import de.ashman.ontrack.feature.user.domain.User
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
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

    @Enumerated(EnumType.STRING)
    var status: TrackStatus,

    @Embedded
    val media: Media,

    @ManyToOne(fetch = FetchType.LAZY)
    val user: User,

    @OneToOne(mappedBy = "tracking", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val review: Review? = null,

    @OneToOne(mappedBy = "tracking", cascade = [CascadeType.REMOVE], orphanRemoval = true, fetch = FetchType.LAZY)
    var post: Post? = null,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    var createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant = Instant.now(),
)
