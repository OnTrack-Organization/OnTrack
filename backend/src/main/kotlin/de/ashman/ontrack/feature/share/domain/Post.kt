package de.ashman.ontrack.feature.share.domain

import de.ashman.ontrack.feature.review.domain.Review
import de.ashman.ontrack.feature.tracking.domain.Tracking
import de.ashman.ontrack.feature.user.domain.User
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "posts")
data class Post(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    val user: User,

    @OneToOne(fetch = FetchType.LAZY)
    val tracking: Tracking,

    @OneToOne(fetch = FetchType.LAZY)
    val review: Review? = null,

    @Column(name = "updated_at")
    val updatedAt: Instant = Instant.now()
)
