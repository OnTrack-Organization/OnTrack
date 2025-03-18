package de.ashman.ontrack.features.usecase

import de.ashman.ontrack.domain.user.Friend
import de.ashman.ontrack.features.common.SharedUiManager
import de.ashman.ontrack.repository.firestore.FriendRepository
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.feed_friend_removed

class RemoveFriendUseCase(
    private val friendRepository: FriendRepository,
    private val sharedUiManager: SharedUiManager,
) {
    suspend operator fun invoke(friend: Friend) {
        friendRepository.removeFriend(friend)
        sharedUiManager.showSnackbar(Res.string.feed_friend_removed)
    }
}