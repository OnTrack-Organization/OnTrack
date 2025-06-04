package de.ashman.ontrack.feature.user.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "users")
data class User(
    @Id
    val id: String,

    var name: String,

    @Column(unique = true, nullable = true)
    var username: String? = null,

    @Column(unique = true)
    var email: String,

    @Column(name = "profile_picture_url")
    var profilePictureUrl: String,

    @Column(name = "fcm_token")
    var fcmToken: String? = null,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    var createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant = Instant.now(),
)
