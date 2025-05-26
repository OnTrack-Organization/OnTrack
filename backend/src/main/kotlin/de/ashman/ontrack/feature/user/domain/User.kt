package de.ashman.ontrack.feature.user.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
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

    @Column(name = "updated_at")
    var updatedAt: Instant = Instant.now(),
) {
    fun updateFcmToken(token: String) {
        fcmToken = token
        updateLastModified()
    }

    fun clearFcmToken() {
        fcmToken = null
        updateLastModified()
    }

    fun updateAccountSettings(name: String, username: String) {
        this.name = name
        this.username = username
        updateLastModified()
    }

    fun changeProfilePicture(url: String) {
        profilePictureUrl = url
        updateLastModified()
    }

    private fun updateLastModified() {
        updatedAt = Instant.now()
    }
}

