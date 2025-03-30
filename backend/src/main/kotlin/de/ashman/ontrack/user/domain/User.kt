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

    @Column(name = "email", unique = true)
    var email: String = email
        private set

    @Column(name = "image_url")
    var profilePictureUrl: String = profilePictureUrl
        private set

    @Column(name = "updated_at")
    private var updatedAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "username", unique = true, nullable = true)
    var username: String? = null
        private set

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

    fun updateName(name: String) {
        this.name = name
        updateLastModified()
    }

    fun updateUsername(username: String) {
        this.username = username
        updateLastModified()
    }

    fun changeProfilePictureUrl(url: String) {
        profilePictureUrl = url
        updateLastModified()
    }

    private fun updateLastModified() {
        updatedAt = LocalDateTime.now()
    }
}
