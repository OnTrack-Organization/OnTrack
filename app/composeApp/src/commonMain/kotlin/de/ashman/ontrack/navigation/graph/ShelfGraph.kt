package de.ashman.ontrack.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.features.settings.SettingsScreen
import de.ashman.ontrack.features.settings.SettingsViewModel
import de.ashman.ontrack.features.shelf.OtherUserShelf
import de.ashman.ontrack.features.shelf.ShelfScreen
import de.ashman.ontrack.features.shelf.ShelfViewModel
import de.ashman.ontrack.features.shelflist.ShelfListScreen
import de.ashman.ontrack.features.shelflist.ShelfListViewModel
import de.ashman.ontrack.navigation.CustomNavType
import de.ashman.ontrack.navigation.Route
import de.ashman.ontrack.repository.firestore.FirestoreUserRepository
import kotlin.reflect.typeOf

fun NavGraphBuilder.shelfGraph(
    navController: NavController,
    shelfViewModel: ShelfViewModel,
    shelfListViewModel: ShelfListViewModel,
    settingsViewModel: SettingsViewModel,
    commonUiManager: CommonUiManager,
    firestoreUserRepository: FirestoreUserRepository,
    clearViewModels: () -> Unit,
) {
    composable<Route.Shelf> {
        ShelfScreen(
            viewModel = shelfViewModel,
            commonUiManager = commonUiManager,
            userId = firestoreUserRepository.currentUserId,
            onClickMoreMedia = { mediaType -> navController.navigate(Route.ShelfList(firestoreUserRepository.currentUserId, mediaType)) },
            onClickItem = { mediaNav -> navController.navigate(Route.Detail(mediaNav)) },
            onSettings = { navController.navigate(Route.Settings) },
        )
    }

    composable<Route.ShelfList>(
        typeMap = mapOf(
            typeOf<MediaType>() to CustomNavType.MediaTypeNavType,
        )
    ) { backStackEntry ->
        val shelfList: Route.ShelfList = backStackEntry.toRoute()

        ShelfListScreen(
            viewModel = shelfListViewModel,
            userId = shelfList.userId,
            mediaType = shelfList.mediaType,
            onClickItem = { mediaNavItems -> navController.navigate(Route.Detail(mediaNavItems)) },
            onBack = { navController.popBackStack() },
        )
    }

    composable<Route.OtherShelf> { backStackEntry ->
        val otherShelf: Route.OtherShelf = backStackEntry.toRoute()

        OtherUserShelf(
            viewModel = shelfViewModel,
            commonUiManager = commonUiManager,
            userId = otherShelf.userId,
            onClickMore = { mediaType -> navController.navigate(Route.ShelfList(otherShelf.userId, mediaType)) },
            onClickItem = { mediaNavItem -> navController.navigate(Route.Detail(mediaNavItem)) },
            onBack = { navController.popBackStack() },
        )
    }

    composable<Route.Settings> {
        SettingsScreen(
            viewModel = settingsViewModel,
            commonUiManager = commonUiManager,
            onBack = { navController.popBackStack() },
            clearAndNavigateToStart = {
                clearViewModels()

                navController.navigate(Route.Start) {
                    popUpTo(Route.Share) { inclusive = true } // Clear backstack
                }
            }
        )
    }
}