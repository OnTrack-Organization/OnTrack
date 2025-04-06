package de.ashman.ontrack.user.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "users")
final class User(
    id: String,
    name: String,
    email: String,
    profilePictureUrl: String
) {
    @Id
    @Column(name = "id")
    val id: String = id

    @Column(name = "name")
    var name: String = name
        private set

    @Column(name = "username", unique = true, nullable = true)
    var username: String? = null
        private set

    @Column(name = "email", unique = true)
    var email: String = email
        private set

    @Column(name = "profile_picture_url")
    var profilePictureUrl: String = profilePictureUrl
        private set

    @Column(name = "updated_at")
    private var updatedAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "fcm_token")
    var fcmToken: String? = null
        private set

    fun updateFcmToken(token: String) {
        fcmToken = token
        updateLastModified()
    }

    fun clearFcmToken() {
        fcmToken = null
    }

    fun updateAccountSettings(name: String, username: String) {
        this.name = name
        this.username = username
        updateLastModified()
    }

    fun updateProfilePicture(url: String) {
        profilePictureUrl = url
        updateLastModified()
    }

    private fun updateLastModified() {
        updatedAt = LocalDateTime.now()
    }
}
