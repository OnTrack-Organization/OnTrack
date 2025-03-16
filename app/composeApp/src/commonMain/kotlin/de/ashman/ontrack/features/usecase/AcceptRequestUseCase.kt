package de.ashman.ontrack.features.usecase

import de.ashman.ontrack.domain.user.FriendRequest
import de.ashman.ontrack.features.common.SharedUiManager
import de.ashman.ontrack.notification.NotificationService
import de.ashman.ontrack.repository.CurrentUserRepository
import de.ashman.ontrack.repository.firestore.FriendRepository
import ontrack.app.composeapp.generated.resources.Res
import ontrack.app.composeapp.generated.resources.feed_request_accepted
import ontrack.app.composeapp.generated.resources.notifications_request_accepted_body
import ontrack.app.composeapp.generated.resources.notifications_request_accepted_title
import org.jetbrains.compose.resources.getString

class AcceptRequestUseCase(
    private val friendRepository: FriendRepository,
    private val notificationService: NotificationService,
    private val sharedUiManager: SharedUiManager,
    private val currentUserRepository: CurrentUserRepository,
) {
    suspend operator fun invoke(friendRequest: FriendRequest) {
        friendRepository.acceptRequest(friendRequest)

        val currentUser = currentUserRepository.getCurrentUser()
        notificationService.sendPushNotification(
            userId = friendRequest.userId,
            title = getString(Res.string.notifications_request_accepted_title),
            body = getString(Res.string.notifications_request_accepted_body, currentUser.name),
        )

        sharedUiManager.showSnackbar(Res.string.feed_request_accepted)
    }
}