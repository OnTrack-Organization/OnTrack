package de.ashman.ontrack.feature.friend.service

import de.ashman.ontrack.feature.friend.domain.FriendRequest
import de.ashman.ontrack.feature.friend.domain.FriendRequestStatus
import de.ashman.ontrack.feature.friend.domain.exception.FriendRequestPendingException
import de.ashman.ontrack.feature.friend.repository.FriendRequestRepository
import de.ashman.ontrack.feature.notification.service.NotificationService
import de.ashman.ontrack.feature.user.domain.User
import de.ashman.ontrack.feature.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class FriendRequestService(
    private val friendRequestRepository: FriendRequestRepository,
    private val userRepository: UserRepository,
    private val friendService: FriendService,
    private val notificationService: NotificationService
) {

    fun sendRequest(senderId: String, receiverId: String) {
        if (!userRepository.existsById(receiverId)) throw NoSuchElementException("User not found")

        val sender = userRepository.getReferenceById(senderId)
        val receiver = userRepository.getReferenceById(receiverId)

        val duplicateRequest = friendRequestRepository.findFriendRequestBySenderAndReceiverAndStatus(sender, receiver, FriendRequestStatus.PENDING)
        val reversedRequest = friendRequestRepository.findFriendRequestBySenderAndReceiverAndStatus(receiver, sender, FriendRequestStatus.PENDING)

        if (duplicateRequest != null || reversedRequest != null) {
            throw FriendRequestPendingException()
        }

        val request = FriendRequest(sender = sender, receiver = receiver)
        friendRequestRepository.save(request)

        notificationService.createFriendRequestReceived(sender, receiver)
    }

    fun acceptRequest(currentUserId: String, senderId: String) {
        val sender = userRepository.getReferenceById(senderId)
        val receiver = userRepository.getReferenceById(currentUserId)

        val request = findBySenderAndReceiver(sender, receiver) ?: throw NoSuchElementException("Friend request not found")

        request.accept()

        friendService.beginFriendship(sender, receiver)

        notificationService.createFriendRequestAccepted(acceptor = sender, originalSender = receiver)
    }

    fun declineRequest(currentUserId: String, senderId: String) {
        val sender = userRepository.getReferenceById(senderId)
        val receiver = userRepository.getReferenceById(currentUserId)

        val request = findBySenderAndReceiver(sender, receiver) ?: throw NoSuchElementException("Friend request not found")

        request.decline()
    }

    fun cancelRequest(senderId: String, receiverId: String) {
        val sender = userRepository.getReferenceById(senderId)
        val receiver = userRepository.getReferenceById(receiverId)

        val request = findBySenderAndReceiver(sender, receiver) ?: throw NoSuchElementException("Friend request not found")

        request.cancel()
    }

    fun findBySenderAndReceiver(sender: User, receiver: User): FriendRequest? {
        return friendRequestRepository.findFriendRequestBySenderAndReceiverAndStatus(
            sender = sender,
            receiver = receiver,
            status = FriendRequestStatus.PENDING
        )?.takeIf { it.status == FriendRequestStatus.PENDING }
    }
}
