package de.ashman.ontrack.feature.friend.repository

import de.ashman.ontrack.feature.friend.domain.FriendRequest
import de.ashman.ontrack.feature.friend.domain.FriendRequestStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface FriendRequestRepository : JpaRepository<FriendRequest, UUID> {
    fun findFriendRequestBySenderIdAndReceiverIdAndStatus(sender: String, receiver: String, status: FriendRequestStatus): FriendRequest?

    @Query("select f.receiverId from FriendRequest f where f.senderId = :sender and f.status = 'PENDING'")
    fun findReceiversOfPendingRequests(@Param("sender") sender: String): List<String>

    @Query("select f.senderId from FriendRequest f where f.receiverId = :receiver and f.status = 'PENDING'")
    fun findSendersOfPendingRequests(@Param("receiver") receiver: String): List<String>
}
