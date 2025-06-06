package de.ashman.ontrack.feature.user.domain

import Notification
import de.ashman.ontrack.feature.friend.domain.FriendRequest
import de.ashman.ontrack.feature.friend.domain.Friendship
import de.ashman.ontrack.feature.recommendation.domain.Recommendation
import de.ashman.ontrack.feature.review.domain.Review
import de.ashman.ontrack.feature.tracking.domain.Tracking
import jakarta.persistence.*
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

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val trackings: MutableList<Tracking> = mutableListOf(),

    @OneToMany(mappedBy = "sender", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    val sentRecommendations: MutableList<Recommendation> = mutableListOf(),

    @OneToMany(mappedBy = "receiver", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    val receivedRecommendations: MutableList<Recommendation> = mutableListOf(),

    @OneToMany(mappedBy = "sender", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    val sentNotifications: MutableList<Notification> = mutableListOf(),

    @OneToMany(mappedBy = "receiver", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    val receivedNotifications: MutableList<Notification> = mutableListOf(),

    @OneToMany(mappedBy = "user1", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    val friendshipsAsUser1: MutableList<Friendship> = mutableListOf(),

    @OneToMany(mappedBy = "user2", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    val friendshipsAsUser2: MutableList<Friendship> = mutableListOf(),

    @OneToMany(mappedBy = "sender", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    val friendRequestsSent: MutableList<FriendRequest> = mutableListOf(),

    @OneToMany(mappedBy = "receiver", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    val friendRequestsReceived: MutableList<FriendRequest> = mutableListOf(),

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    var createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant = Instant.now(),
)
