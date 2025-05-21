package de.ashman.ontrack.feature.friend.repository

import de.ashman.ontrack.feature.friend.domain.Friendship
import org.springframework.stereotype.Service

@Service
class FriendshipService(
    private val friendshipRepository: FriendshipRepository,
) {
    fun beginFriendship(userId1: String, userId2: String) {
        val newFriendship = Friendship.begin(userId1, userId2)
        friendshipRepository.save(newFriendship)
    }

    fun endFriendship(userId1: String, userId2: String) {
        val (first, second) = if (userId1 < userId2) userId1 to userId2 else userId2 to userId1
        friendshipRepository.deleteByUserId1AndUserId2(first, second)
    }

    fun getFriendIds(userId: String): List<String> = friendshipRepository.findFriendsOfUser(userId)

    fun areFriends(userId1: String, userId2: String): Boolean {
        val (first, second) = if (userId1 < userId2) userId1 to userId2 else userId2 to userId1

        return friendshipRepository.existsByUserId1AndUserId2(first, second)
    }
}
