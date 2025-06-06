package de.ashman.ontrack.feature.review.domain

import de.ashman.ontrack.feature.tracking.domain.Tracking
import de.ashman.ontrack.feature.user.domain.User
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
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

    var rating: Double,

    var title: String?,

    var description: String?,

    @OneToOne(fetch = FetchType.LAZY)
    val tracking: Tracking,

    @ManyToOne(fetch = FetchType.LAZY)
    val user: User,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    var createdAt: Instant = Instant.now(),

    @UpdateTimestamp
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
    }
}