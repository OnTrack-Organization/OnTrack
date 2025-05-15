package de.ashman.ontrack.user.infrastructure

import de.ashman.ontrack.user.domain.model.FriendRequest
import de.ashman.ontrack.user.domain.model.FriendRequestStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface FriendRequestJpaRepository : JpaRepository<FriendRequest, UUID> {
    fun findFriendRequestBySenderIdAndReceiverIdAndStatus(sender: String, receiver: String, status: FriendRequestStatus): FriendRequest?

    @Query("select f.receiverId from FriendRequest f where f.senderId = :sender and f.status = 'PENDING'")
    fun findReceiversOfPendingRequests(@Param("sender") sender: String): List<String>

    @Query("select f.senderId from FriendRequest f where f.receiverId = :receiver and f.status = 'PENDING'")
    fun findSendersOfPendingRequests(@Param("receiver") receiver: String): List<String>
}
