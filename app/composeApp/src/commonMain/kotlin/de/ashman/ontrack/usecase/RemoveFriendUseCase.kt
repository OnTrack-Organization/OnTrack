package de.ashman.ontrack.usecase

import de.ashman.ontrack.domain.user.Friend
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.repository.firestore.FriendRepository
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.feed_friend_removed

class RemoveFriendUseCase(
    private val friendRepository: FriendRepository,
    private val commonUiManager: CommonUiManager,
) {
    suspend operator fun invoke(friend: Friend) {
        friendRepository.removeFriend(friend)
        commonUiManager.showSnackbar(Res.string.feed_friend_removed)
    }
}