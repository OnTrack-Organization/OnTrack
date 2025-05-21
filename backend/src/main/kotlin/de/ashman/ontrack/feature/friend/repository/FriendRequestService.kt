package de.ashman.ontrack.feature.friend.repository

import de.ashman.ontrack.feature.friend.domain.FriendRequest
import de.ashman.ontrack.feature.friend.domain.exception.FriendRequestPendingException
import de.ashman.ontrack.feature.friend.domain.FriendRequestStatus
import org.springframework.stereotype.Service

@Service
class FriendRequestService(
    private val friendRequestRepository: FriendRequestRepository
) {
    fun findBySenderAndReceiver(sender: String, receiver: String): FriendRequest? {
        val request = friendRequestRepository.findFriendRequestBySenderIdAndReceiverIdAndStatus(
            sender = sender,
            receiver = receiver,
            status = FriendRequestStatus.PENDING
        )

        return if (request?.getStatus() === FriendRequestStatus.PENDING) request else null
    }

    fun save(request: FriendRequest) {
        val duplicateRequest = friendRequestRepository.findFriendRequestBySenderIdAndReceiverIdAndStatus(
            sender = request.senderId,
            receiver = request.receiverId,
            status = FriendRequestStatus.PENDING
        )
        val reversedRequest = friendRequestRepository.findFriendRequestBySenderIdAndReceiverIdAndStatus(
            sender = request.receiverId,
            receiver = request.senderId,
            status = FriendRequestStatus.PENDING
        )

        if (duplicateRequest === null && reversedRequest === null) {
            friendRequestRepository.save(request)
        } else {
            throw FriendRequestPendingException()
        }
    }

    fun findSendersOfReceivedRequests(receiver: String): List<String> = friendRequestRepository.findSendersOfPendingRequests(receiver)

    fun findReceiversOfSentRequests(sender: String): List<String> = friendRequestRepository.findReceiversOfPendingRequests(sender)
}
