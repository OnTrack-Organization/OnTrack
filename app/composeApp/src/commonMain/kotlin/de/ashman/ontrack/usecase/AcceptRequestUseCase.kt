package de.ashman.ontrack.usecase

import de.ashman.ontrack.datastore.UserDataStore
import de.ashman.ontrack.domain.user.FriendRequest
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.notification.NotificationService
import de.ashman.ontrack.repository.firestore.FriendRepository
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.feed_request_accepted
import ontrack.composeapp.generated.resources.notifications_request_accepted_body
import ontrack.composeapp.generated.resources.notifications_request_accepted_title
import org.jetbrains.compose.resources.getString

class AcceptRequestUseCase(
    private val friendRepository: FriendRepository,
    private val notificationService: NotificationService,
    private val commonUiManager: CommonUiManager,
    private val userDataStore: UserDataStore,
) {
    suspend operator fun invoke(friendRequest: FriendRequest) {
        friendRepository.acceptRequest(friendRequest)

        val currentUser = userDataStore.getCurrentUser()
        notificationService.sendPushNotification(
            userId = friendRequest.userId,
            title = getString(Res.string.notifications_request_accepted_title),
            body = getString(Res.string.notifications_request_accepted_body, currentUser.name),
        )

        commonUiManager.showSnackbar(Res.string.feed_request_accepted)
    }
}