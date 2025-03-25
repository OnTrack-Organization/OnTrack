package de.ashman.ontrack.usecase

import de.ashman.ontrack.domain.user.FriendRequest
import de.ashman.ontrack.features.common.SharedUiManager
import de.ashman.ontrack.repository.firestore.FriendRepository
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.feed_request_declined

class DeclineRequestUseCase(
    private val friendRepository: FriendRepository,
    private val sharedUiManager: SharedUiManager,
) {
    suspend operator fun invoke(friendRequest: FriendRequest) {
        friendRepository.declineRequest(friendRequest)
        sharedUiManager.showSnackbar(Res.string.feed_request_declined)
    }
}