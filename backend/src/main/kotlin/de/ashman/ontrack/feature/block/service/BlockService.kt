package de.ashman.ontrack.feature.block.service

import de.ashman.ontrack.feature.block.domain.Blocking
import de.ashman.ontrack.feature.block.repository.BlockRepository
import de.ashman.ontrack.feature.friend.domain.FriendRequestStatus
import de.ashman.ontrack.feature.friend.repository.FriendRepository
import de.ashman.ontrack.feature.friend.repository.FriendRequestRepository
import de.ashman.ontrack.feature.user.domain.User
import de.ashman.ontrack.feature.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BlockService(
    private val blockRepository: BlockRepository,
    private val userRepository: UserRepository,
    private val friendRepository: FriendRepository,
    private val friendRequestRepository: FriendRequestRepository,
) {

    @Transactional
    fun blockUser(blockerId: String, blockedId: String) {
        val blocker = userRepository.getReferenceById(blockerId)
        val blocked = userRepository.getReferenceById(blockedId)

        if (!blockRepository.existsByBlockerAndBlocked(blocker, blocked)) {
            val blocking = Blocking(blocker = blocker, blocked = blocked)
            blockRepository.save(blocking)

            // Decline friend request if it exists
            friendRequestRepository.findFriendRequestBySenderIdAndReceiverIdAndStatus(blockedId, blockerId, FriendRequestStatus.PENDING)?.decline()

            // Delete friendship if it exists
            if (friendRepository.areFriends(blockerId, blockedId)) {
                friendRepository.deleteFriendshipBetween(blockerId, blockedId)
            }
        }
    }

    @Transactional
    fun unblockUser(blockerId: String, blockedId: String) {
        blockRepository.deleteByBlockerIdAndBlockedId(blockerId, blockedId)
    }

    fun getBlockedUsers(blockerId: String): Set<User> = blockRepository.findBlockedUsersByBlockerId(blockerId)

    fun isBlocked(blockerId: String, blockedId: String): Boolean {
        val blocker = userRepository.getReferenceById(blockerId)
        val blocked = userRepository.getReferenceById(blockedId)
        return blockRepository.existsByBlockerAndBlocked(blocker, blocked)
    }
}
