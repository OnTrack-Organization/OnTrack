package de.ashman.ontrack.feature.user.service

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
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val trackingRepository: TrackingRepository,
    private val friendRepository: FriendRepository,
    private val friendService: FriendService,
    private val friendRequestRepository: FriendRequestRepository,
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

        val trackings = trackingRepository.getTrackingsByUserId(otherUser.id)

        val friendStatus = when {
            friendRepository.areFriends(currentUser.id, otherUser.id) -> FriendStatus.FRIEND
            friendRequestRepository.findFriendRequestBySenderAndReceiverAndStatus(currentUser, otherUser, FriendRequestStatus.PENDING) != null -> FriendStatus.REQUEST_SENT
            friendRequestRepository.findFriendRequestBySenderAndReceiverAndStatus(otherUser, currentUser, FriendRequestStatus.PENDING) != null -> FriendStatus.REQUEST_RECEIVED
            else -> FriendStatus.STRANGER
        }

        return UserProfileDto(
            user = OtherUserDto(otherUser.toDto(), friendStatus),
            trackings = trackings.map { it.toDto() }
        )
    }
}
