package de.ashman.ontrack.tracking.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "user__user")
data class User(
    @Column(name = "fcm_token")
    val fcmToken: String,

    @Column(name = "name")
    val name: String,

    @Column(name = "username")
    val username: String,

    @Column(name = "email")
    val email: String,

    @Column(name = "image_url")
    val imageUrl: String,

    ) {
    @Id
    @Column(name = "id")
    val id: UUID = UUID.randomUUID()

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
}
