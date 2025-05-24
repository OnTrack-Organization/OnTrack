package de.ashman.ontrack.database.review

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.ashman.ontrack.domain.review.Review

@Entity(tableName = "review")
data class ReviewEntity(
    @PrimaryKey
    val id: String,
    val trackingId: String,
    val rating: Double,
    val title: String?,
    val description: String?,
    val timestamp: Long,
)

fun ReviewEntity.toDomain() = Review(
    id = id,
    trackingId = trackingId,
    rating = rating,
    title = title,
    description = description,
    timestamp = timestamp,
)
