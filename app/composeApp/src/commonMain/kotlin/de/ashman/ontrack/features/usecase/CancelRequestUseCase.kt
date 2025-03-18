package de.ashman.ontrack.features.usecase

import de.ashman.ontrack.domain.user.FriendRequest
import de.ashman.ontrack.features.common.SharedUiManager
import de.ashman.ontrack.repository.firestore.FriendRepository
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.feed_request_cancelled

class CancelRequestUseCase(
    private val friendRepository: FriendRepository,
    private val sharedUiManager: SharedUiManager,
) {
    suspend operator fun invoke(friendRequest: FriendRequest) {
        friendRepository.cancelRequest(friendRequest)
        sharedUiManager.showSnackbar(Res.string.feed_request_cancelled)
    }
}
