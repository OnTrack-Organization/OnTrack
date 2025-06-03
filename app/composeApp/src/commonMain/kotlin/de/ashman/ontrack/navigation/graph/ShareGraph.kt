package de.ashman.ontrack.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.features.friend.FriendsViewModel
import de.ashman.ontrack.features.notification.NotificationScreen
import de.ashman.ontrack.features.notification.NotificationViewModel
import de.ashman.ontrack.features.share.PostDetailScreen
import de.ashman.ontrack.features.share.PostViewModel
import de.ashman.ontrack.features.share.ShareScreen
import de.ashman.ontrack.navigation.Route

fun NavGraphBuilder.shareGraph(
    navController: NavController,
    postViewModel: PostViewModel,
    friendsViewModel: FriendsViewModel,
    notificationViewModel: NotificationViewModel,
    commonUiManager: CommonUiManager,
) {
    composable<Route.Share> {
        ShareScreen(
            postViewModel = postViewModel,
            friendsViewModel = friendsViewModel,
            notificationViewModel = notificationViewModel,
            commonUiManager = commonUiManager,
            onClickPost = { navController.navigate(Route.PostDetail(it)) },
            onClickCover = { mediaNav -> navController.navigate(Route.Detail(mediaNav)) },
            onClickUser = { userId ->
                commonUiManager.hideSheet()
                navController.navigateToShelf(userId)
            },
            onClickNotifications = { navController.navigate(Route.Notifications) },
        )
    }

    composable<Route.PostDetail> { backStackEntry ->
        val postDetail: Route.PostDetail = backStackEntry.toRoute()

        PostDetailScreen(
            viewModel = postViewModel,
            postId = postDetail.postId,
            onClickUser = { userId -> navController.navigateToShelf(userId) },
            onClickMedia = { navController.navigate(Route.Detail(it)) },
            onBack = { navController.popBackStack() },
        )
    }

    composable<Route.Notifications> {
        NotificationScreen(
            viewModel = notificationViewModel,
            commonUiManager = commonUiManager,
            onBack = { navController.popBackStack() },
            onClickPost = { navController.navigate(Route.PostDetail(it)) },
            onClickUser = { userId -> navController.navigateToShelf(userId) },
            onClickMedia = { navController.navigate(Route.Detail(it)) }
        )
    }
}