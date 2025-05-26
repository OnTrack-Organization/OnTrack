package de.ashman.ontrack.feature.friend.service

import de.ashman.ontrack.feature.friend.domain.Friendship
import de.ashman.ontrack.feature.friend.repository.FriendRepository
import de.ashman.ontrack.feature.user.domain.User
import de.ashman.ontrack.feature.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class FriendService(
    private val friendRepository: FriendRepository,
    private val userRepository: UserRepository,
) {
    fun beginFriendship(user1: User, user2: User) {
        val newFriendship = Friendship.begin(user1, user2)
        friendRepository.save(newFriendship)
    }

    fun endFriendship(user1: User, user2: User) {
        val (firstId, secondId) = if (user1.id < user2.id) user1.id to user2.id else user2.id to user1.id
        friendRepository.deleteByUser1IdAndUser2Id(firstId, secondId)
    }

    fun getFriendIds(userId: String): List<String> = friendRepository.findFriendIdsOf(userId)

    fun getFriends(user: User): List<User> {
        val friendIds = getFriendIds(user.id)
        return userRepository.findAllById(friendIds)
    }

    fun areFriends(userId1: String, userId2: String): Boolean {
        val (first, second) = if (userId1 < userId2) userId1 to userId2 else userId2 to userId1

        return friendRepository.existsByUser1IdAndUser2Id(first, second)
    }
}
