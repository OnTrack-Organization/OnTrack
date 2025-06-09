package de.ashman.ontrack.feature.friend.service

import de.ashman.ontrack.feature.friend.domain.FriendRequest
import de.ashman.ontrack.feature.friend.domain.FriendRequestStatus
import de.ashman.ontrack.feature.friend.repository.FriendRequestRepository
import de.ashman.ontrack.feature.notification.service.NotificationService
import de.ashman.ontrack.feature.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FriendRequestService(
    private val friendRequestRepository: FriendRequestRepository,
    private val userRepository: UserRepository,
    private val friendService: FriendService,
    private val notificationService: NotificationService
) {
    @Transactional
    fun sendRequest(senderId: String, receiverId: String) {
        val sender = userRepository.getReferenceById(senderId)
        val receiver = userRepository.getReferenceById(receiverId)

        val request = FriendRequest(sender = sender, receiver = receiver)

        friendRequestRepository.save(request)

        notificationService.createFriendRequestReceived(sender, receiver)
    }

    @Transactional
    fun acceptRequest(currentUserId: String, senderId: String) {
        val sender = userRepository.getReferenceById(senderId)
        val receiver = userRepository.getReferenceById(currentUserId)

        val request = friendRequestRepository.findFriendRequestBySenderIdAndReceiverIdAndStatus(senderId, currentUserId, FriendRequestStatus.PENDING)
            ?: throw NoSuchElementException("Friend request not found")

        request.accept()

        friendService.beginFriendship(sender, receiver)

        notificationService.createFriendRequestAccepted(acceptor = sender, originalSender = receiver)
    }

    @Transactional
    fun declineRequest(currentUserId: String, senderId: String) {
        val request = friendRequestRepository.findFriendRequestBySenderIdAndReceiverIdAndStatus(senderId, currentUserId, FriendRequestStatus.PENDING)
            ?: throw NoSuchElementException("Friend request not found")

        request.decline()
    }

    @Transactional
    fun cancelRequest(senderId: String, receiverId: String) {
        val request = friendRequestRepository.findFriendRequestBySenderIdAndReceiverIdAndStatus(senderId, receiverId, FriendRequestStatus.PENDING)
            ?: throw NoSuchElementException("Friend request not found")

        request.cancel()
    }
}
