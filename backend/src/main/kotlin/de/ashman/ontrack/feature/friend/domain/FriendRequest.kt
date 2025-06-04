package de.ashman.ontrack.feature.friend.domain

import de.ashman.ontrack.feature.friend.domain.exception.FriendRequestAlreadyProcessedException
import de.ashman.ontrack.feature.user.domain.User
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "friend_requests")
class FriendRequest(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Enumerated(EnumType.STRING)
    var status: FriendRequestStatus = FriendRequestStatus.PENDING,

    @ManyToOne(fetch = FetchType.LAZY)
    val sender: User,

    @ManyToOne(fetch = FetchType.LAZY)
    val receiver: User,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    var createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant = Instant.now(),
) {
    fun accept() {
        if (status !== FriendRequestStatus.PENDING) throw FriendRequestAlreadyProcessedException()

        status = FriendRequestStatus.ACCEPTED
    }

    fun decline() {
        if (status !== FriendRequestStatus.PENDING) throw FriendRequestAlreadyProcessedException()

        status = FriendRequestStatus.DECLINED
    }

    fun cancel() {
        if (status !== FriendRequestStatus.PENDING) throw FriendRequestAlreadyProcessedException()

        status = FriendRequestStatus.CANCELLED
    }
}
