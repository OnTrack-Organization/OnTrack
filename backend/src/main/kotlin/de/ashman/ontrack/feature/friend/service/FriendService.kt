package de.ashman.ontrack.feature.friend.service

import de.ashman.ontrack.feature.friend.domain.FriendStatus
import de.ashman.ontrack.feature.friend.domain.Friendship
import de.ashman.ontrack.feature.friend.repository.FriendRepository
import de.ashman.ontrack.feature.friend.repository.FriendRequestRepository
import de.ashman.ontrack.feature.user.controller.dto.OtherUserDto
import de.ashman.ontrack.feature.user.controller.dto.UserDto
import de.ashman.ontrack.feature.user.controller.dto.toDto
import de.ashman.ontrack.feature.user.domain.User
import de.ashman.ontrack.feature.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class FriendService(
    private val friendRepository: FriendRepository,
    private val userRepository: UserRepository,
    private val friendRequestRepository: FriendRequestRepository,
) {
    fun beginFriendship(user1: User, user2: User) {
        val newFriendship = Friendship.begin(user1, user2)
        friendRepository.save(newFriendship)
    }

    fun endFriendship(userId: String, friendId: String) = friendRepository.deleteFriendshipBetween(userId, friendId)

    fun getFriendIds(userId: String): List<String> = friendRepository.findFriendIdsOf(userId)

    fun getFriends(user: User): List<User> {
        val friendIds = getFriendIds(user.id)
        return userRepository.findAllById(friendIds)
    }

    fun getFriendDtos(userId: String): List<UserDto> {
        val user = userRepository.getReferenceById(userId)
        return getFriends(user).map { it.toDto() }
    }

    fun getFriendsAndFriendRequests(userId: String): List<OtherUserDto> {
        val user = userRepository.getReferenceById(userId)

        val friends = getFriends(user)
        val sentRequests = friendRequestRepository.findReceiversOfPendingRequests(user)
        val receivedRequests = friendRequestRepository.findSendersOfPendingRequests(user)

        val friendDtos = friends.map { OtherUserDto(it.toDto(), FriendStatus.FRIEND) }
        val sentRequestDtos = sentRequests.map { OtherUserDto(it.toDto(), FriendStatus.REQUEST_SENT) }
        val receivedRequestDtos = receivedRequests.map { OtherUserDto(it.toDto(), FriendStatus.REQUEST_RECEIVED) }

        return friendDtos + sentRequestDtos + receivedRequestDtos
    }
}
