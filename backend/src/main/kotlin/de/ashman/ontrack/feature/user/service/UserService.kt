package de.ashman.ontrack.feature.user.service

import de.ashman.ontrack.feature.block.repository.BlockingRepository
import de.ashman.ontrack.feature.friend.domain.FriendRequestStatus
import de.ashman.ontrack.feature.friend.domain.FriendStatus
import de.ashman.ontrack.feature.friend.repository.FriendRepository
import de.ashman.ontrack.feature.friend.repository.FriendRequestRepository
import de.ashman.ontrack.feature.friend.service.FriendService
import de.ashman.ontrack.feature.tracking.controller.dto.toDto
import de.ashman.ontrack.feature.tracking.repository.TrackingRepository
import de.ashman.ontrack.feature.user.controller.dto.OtherUserDto
import de.ashman.ontrack.feature.user.controller.dto.UserProfileDto
import de.ashman.ontrack.feature.user.controller.dto.toDto
import de.ashman.ontrack.feature.user.domain.User
import de.ashman.ontrack.feature.user.repository.UserRepository
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val trackingRepository: TrackingRepository,
    private val friendRepository: FriendRepository,
    private val friendService: FriendService,
    private val friendRequestRepository: FriendRequestRepository,
    private val blockingRepository: BlockingRepository,
) {
    fun getById(id: String): User = userRepository.getReferenceById(id)

    fun searchOtherUsers(currentUserId: String, searchUsername: String): List<OtherUserDto> {
        val currentUser = userRepository.getReferenceById(currentUserId)

        val friends = friendService.getFriends(currentUser)
        val sentRequests = friendRequestRepository.findReceiversOfPendingRequests(currentUser)
        val receivedRequests = friendRequestRepository.findSendersOfPendingRequests(currentUser)

        return userRepository
            .findTop10ByUsernameContainingIgnoreCaseAndIdNotOrderByUsernameAsc(searchUsername, currentUser.id)
            .filterNot { it in friends }
            // Filter out blocker user, if they blocked current user
            .filterNot { blockingRepository.existsByBlockerAndBlocked(it, currentUser) }
            .map { user ->
                val status = when (user) {
                    in friends -> FriendStatus.FRIEND
                    in sentRequests -> FriendStatus.REQUEST_SENT
                    in receivedRequests -> FriendStatus.REQUEST_RECEIVED
                    else -> FriendStatus.STRANGER
                }
                OtherUserDto(user.toDto(), status)
            }
    }

    fun getUserProfile(currentUserId: String, otherUserId: String): UserProfileDto {
        val currentUser = getById(currentUserId)
        val otherUser = getById(otherUserId)

        val isBlockedByOtherUser = blockingRepository.existsByBlockerAndBlocked(blocker = otherUser, blocked = currentUser)
        if (isBlockedByOtherUser) {
            throw AccessDeniedException("You are blocked by this user and cannot view their profile.")
        }

        val trackings = trackingRepository.getTrackingsByUserId(otherUser.id)

        val isBlocked = blockingRepository.existsByBlockerAndBlocked(currentUser, otherUser)

        val friendStatus = when {
            friendRepository.areFriends(currentUser.id, otherUser.id) -> FriendStatus.FRIEND
            friendRequestRepository.findFriendRequestBySenderIdAndReceiverIdAndStatus(currentUser.id, otherUser.id, FriendRequestStatus.PENDING) != null -> FriendStatus.REQUEST_SENT
            friendRequestRepository.findFriendRequestBySenderIdAndReceiverIdAndStatus(otherUser.id, currentUser.id, FriendRequestStatus.PENDING) != null -> FriendStatus.REQUEST_RECEIVED
            else -> FriendStatus.STRANGER
        }

        return UserProfileDto(
            user = OtherUserDto(otherUser.toDto(), friendStatus),
            trackings = trackings.map { it.toDto() },
            blocked = isBlocked,
        )
    }
}
