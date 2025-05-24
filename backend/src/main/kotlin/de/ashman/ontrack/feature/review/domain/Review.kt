package de.ashman.ontrack.feature.review.domain

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(
    name = "reviews",
    uniqueConstraints = [UniqueConstraint(columnNames = ["tracking_id"])]
)
final class Review(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "user_id")
    val userId: String,
    @Column(name = "tracking_id")
    val trackingId: UUID,
    var rating: Double,
    var title: String?,
    var description: String?,
    @Column(name = "created_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    fun update(
        rating: Double,
        title: String?,
        description: String?,
    ) {
        this.rating = rating
        this.title = title
        this.description = description
        updatedAt = LocalDateTime.now()
    }
}