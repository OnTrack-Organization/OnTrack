package de.ashman.ontrack.user.infrastructure

import de.ashman.ontrack.user.domain.model.Friendship
import de.ashman.ontrack.user.domain.repository.FriendshipRepository
import org.springframework.stereotype.Repository

@Repository
class FriendshipRepositoryImpl(
    private val friendshipJpaRepository: FriendshipJpaRepository,
) : FriendshipRepository {
    override fun beginFriendship(userId1: String, userId2: String) {
        val newFriendship = Friendship.begin(userId1, userId2)
        friendshipJpaRepository.save(newFriendship)
    }

    override fun endFriendship(userId1: String, userId2: String) {
        val (first, second) = if (userId1 < userId2) userId1 to userId2 else userId2 to userId1
        friendshipJpaRepository.deleteByUserId1AndUserId2(first, second)
    }

    override fun getFriends(userId: String): List<String> {
        return friendshipJpaRepository.findFriendsOfUser(userId)
    }

    override fun areFriends(userId1: String, userId2: String): Boolean {
        val (first, second) = if (userId1 < userId2) userId1 to userId2 else userId2 to userId1

        return friendshipJpaRepository.existsByUserId1AndUserId2(first, second)
    }
}
