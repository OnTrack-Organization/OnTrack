package de.ashman.ontrack.feature.friend.domain

import de.ashman.ontrack.feature.friend.domain.exception.FriendRequestAlreadyProcessedException
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "friends")
class FriendRequest(
    @Column(name = "sender_id", nullable = false)
    val senderId: String,
    @Column(name = "receiver_id", nullable = false)
    val receiverId: String
) {
    @Id
    @Column(name = "id", nullable = false)
    private val id: UUID = UUID.randomUUID()

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private var status: FriendRequestStatus = FriendRequestStatus.PENDING

    fun getStatus(): FriendRequestStatus {
        return status
    }

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
