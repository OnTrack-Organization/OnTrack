package de.ashman.ontrack.usecase

import de.ashman.ontrack.domain.user.FriendRequest
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.repository.firestore.FriendRepository
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.feed_request_cancelled

class CancelRequestUseCase(
    private val friendRepository: FriendRepository,
    private val commonUiManager: CommonUiManager,
) {
    suspend operator fun invoke(friendRequest: FriendRequest) {
        friendRepository.cancelRequest(friendRequest)
        commonUiManager.showSnackbar(Res.string.feed_request_cancelled)
    }
}
