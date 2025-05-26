package de.ashman.ontrack.feature.friend.repository

import de.ashman.ontrack.feature.friend.domain.FriendRequest
import de.ashman.ontrack.feature.friend.domain.FriendRequestStatus
import de.ashman.ontrack.feature.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface FriendRequestRepository : JpaRepository<FriendRequest, UUID> {
    fun findFriendRequestBySenderAndReceiverAndStatus(sender: User, receiver: User, status: FriendRequestStatus): FriendRequest?

    @Query("SELECT fr.sender FROM FriendRequest fr WHERE fr.receiver = :receiver AND fr.status = 'PENDING'")
    fun findSendersOfPendingRequests(receiver: User): List<User>

    @Query("SELECT fr.receiver FROM FriendRequest fr WHERE fr.sender = :sender AND fr.status = 'PENDING'")
    fun findReceiversOfPendingRequests(sender: User): List<User>
}
