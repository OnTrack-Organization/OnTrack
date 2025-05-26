package de.ashman.ontrack.feature.friend.service

import de.ashman.ontrack.feature.friend.domain.FriendRequest
import de.ashman.ontrack.feature.friend.domain.exception.FriendRequestPendingException
import de.ashman.ontrack.feature.friend.domain.FriendRequestStatus
import de.ashman.ontrack.feature.friend.repository.FriendRequestRepository
import de.ashman.ontrack.feature.user.domain.User
import org.springframework.stereotype.Service

@Service
class FriendRequestService(
    private val friendRequestRepository: FriendRequestRepository
) {
    fun findBySenderAndReceiver(sender: User, receiver: User): FriendRequest? {
        val request = friendRequestRepository.findFriendRequestBySenderAndReceiverAndStatus(
            sender = sender,
            receiver = receiver,
            status = FriendRequestStatus.PENDING
        )

        return if (request?.status == FriendRequestStatus.PENDING) request else null
    }

    fun save(request: FriendRequest) {
        val duplicateRequest = friendRequestRepository.findFriendRequestBySenderAndReceiverAndStatus(
            sender = request.sender,
            receiver = request.receiver,
            status = FriendRequestStatus.PENDING
        )
        val reversedRequest = friendRequestRepository.findFriendRequestBySenderAndReceiverAndStatus(
            sender = request.receiver,
            receiver = request.sender,
            status = FriendRequestStatus.PENDING
        )

        if (duplicateRequest == null && reversedRequest == null) {
            friendRequestRepository.save(request)
        } else {
            throw FriendRequestPendingException()
        }
    }

    fun findSendersOfReceivedRequests(receiver: User): List<User> = friendRequestRepository.findSendersOfPendingRequests(receiver)

    fun findReceiversOfSentRequests(sender: User): List<User> = friendRequestRepository.findReceiversOfPendingRequests(sender)
}
