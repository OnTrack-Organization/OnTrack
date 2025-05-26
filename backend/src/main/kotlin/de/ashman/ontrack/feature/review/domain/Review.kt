package de.ashman.ontrack.feature.review.domain

import de.ashman.ontrack.feature.tracking.domain.Tracking
import de.ashman.ontrack.feature.user.domain.User
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "reviews",
    uniqueConstraints = [UniqueConstraint(columnNames = ["tracking_id"])]
)
data class Review(
    @Id
    val id: UUID = UUID.randomUUID(),
    @ManyToOne(fetch = FetchType.LAZY)
    val user: User,
    @OneToOne(fetch = FetchType.LAZY)
    val tracking: Tracking,

    var rating: Double,
    var title: String?,
    var description: String?,

    @Column(name = "updated_at")
    var updatedAt: Instant = Instant.now(),
) {
    fun update(
        rating: Double,
        title: String?,
        description: String?,
    ) {
        this.rating = rating
        this.title = title
        this.description = description
        updatedAt = Instant.now()
    }
}