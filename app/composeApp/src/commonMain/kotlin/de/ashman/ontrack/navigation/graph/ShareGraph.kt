package de.ashman.ontrack.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.features.notifications.NotificationsScreen
import de.ashman.ontrack.features.notifications.NotificationsViewModel
import de.ashman.ontrack.features.share.ShareScreen
import de.ashman.ontrack.features.share.ShareViewModel
import de.ashman.ontrack.features.share.friend.FriendsViewModel
import de.ashman.ontrack.features.share_detail.ShareDetailScreen
import de.ashman.ontrack.features.share_detail.ShareDetailViewModel
import de.ashman.ontrack.navigation.Route
import de.ashman.ontrack.repository.firestore.FirestoreUserRepository

fun NavGraphBuilder.shareGraph(
    navController: NavController,
    shareViewModel: ShareViewModel,
    friendsViewModel: FriendsViewModel,
    shareDetailViewModel: ShareDetailViewModel,
    notificationsViewModel: NotificationsViewModel,
    commonUiManager: CommonUiManager,
    firestoreUserRepository: FirestoreUserRepository,
) {
    composable<Route.Share> {
        ShareScreen(
            shareViewModel = shareViewModel,
            friendsViewModel = friendsViewModel,
            commonUiManager = commonUiManager,
            onClickShareCard = { navController.navigate(Route.ShareDetail(it)) },
            onClickCover = { mediaNav -> navController.navigate(Route.Detail(mediaNav)) },
            onClickUser = { userId ->
                commonUiManager.hideSheet()
                navController.navigateToShelf(userId, firestoreUserRepository)
            },
            onClickNotifications = { navController.navigate(Route.Notifications) },
        )
    }

    composable<Route.ShareDetail> { backStackEntry ->
        val shareDetail: Route.ShareDetail = backStackEntry.toRoute()

        ShareDetailScreen(
            viewModel = shareDetailViewModel,
            trackingId = shareDetail.trackingId,
            onClickUser = { userId -> navController.navigateToShelf(userId, firestoreUserRepository) },
            onClickMedia = { navController.navigate(Route.Detail(it)) },
            onBack = { navController.popBackStack() },
        )
    }

    composable<Route.Notifications> {
        NotificationsScreen(
            viewModel = notificationsViewModel,
            onBack = { navController.popBackStack() },
            onNotificationClick = { navController.navigate(Route.ShareDetail(it)) },
            onClickUser = { userId -> navController.navigateToShelf(userId, firestoreUserRepository) },
            onClickMedia = { navController.navigate(Route.Detail(it)) }
        )
    }
}