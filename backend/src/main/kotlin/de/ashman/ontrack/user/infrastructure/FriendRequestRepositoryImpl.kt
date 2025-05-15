package de.ashman.ontrack.user.infrastructure

import de.ashman.ontrack.user.domain.model.FriendRequest
import de.ashman.ontrack.user.domain.model.FriendRequestStatus
import de.ashman.ontrack.user.domain.repository.FriendRequestPendingException
import de.ashman.ontrack.user.domain.repository.FriendRequestRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Repository
class FriendRequestRepositoryImpl(
    private val friendRequestJpaRepository: FriendRequestJpaRepository
) : FriendRequestRepository {
    override fun findBySenderAndReceiver(sender: String, receiver: String): FriendRequest? {
        val request = friendRequestJpaRepository.findFriendRequestBySenderIdAndReceiverIdAndStatus(
            sender,
            receiver,
            FriendRequestStatus.PENDING
        )

        return if (request?.getStatus() === FriendRequestStatus.PENDING) request else null
    }

    override fun save(request: FriendRequest) {
        val duplicateRequest = friendRequestJpaRepository.findFriendRequestBySenderIdAndReceiverIdAndStatus(
            request.senderId,
            request.receiverId,
            FriendRequestStatus.PENDING
        )
        val reversedRequest = friendRequestJpaRepository.findFriendRequestBySenderIdAndReceiverIdAndStatus(
            request.receiverId,
            request.senderId,
            FriendRequestStatus.PENDING
        )

        if (duplicateRequest === null && reversedRequest === null) {
            friendRequestJpaRepository.save(request)
        } else {
            throw FriendRequestPendingException()
        }
    }

    override fun findSendersOfReceivedRequests(receiver: String): List<String> {
        return friendRequestJpaRepository.findSendersOfPendingRequests(receiver)
    }

    override fun findReceiversOfSentRequests(sender: String): List<String> {
        return friendRequestJpaRepository.findReceiversOfPendingRequests(sender)
    }
}
