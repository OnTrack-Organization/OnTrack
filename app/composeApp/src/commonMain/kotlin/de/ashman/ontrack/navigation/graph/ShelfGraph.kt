package de.ashman.ontrack.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.features.settings.SettingsScreen
import de.ashman.ontrack.features.settings.SettingsViewModel
import de.ashman.ontrack.features.shelf.OtherShelfScreen
import de.ashman.ontrack.features.shelf.ShelfScreen
import de.ashman.ontrack.features.shelf.ShelfViewModel
import de.ashman.ontrack.features.shelflist.ShelfListScreen
import de.ashman.ontrack.features.shelflist.ShelfListViewModel
import de.ashman.ontrack.navigation.CustomNavType
import de.ashman.ontrack.navigation.Route
import kotlin.reflect.typeOf

fun NavGraphBuilder.shelfGraph(
    currentUserId: String,
    navController: NavController,
    shelfViewModel: ShelfViewModel,
    shelfListViewModel: ShelfListViewModel,
    settingsViewModel: SettingsViewModel,
    commonUiManager: CommonUiManager,
    clearViewModels: () -> Unit,
) {
    composable<Route.Shelf> {
        ShelfScreen(
            viewModel = shelfViewModel,
            commonUiManager = commonUiManager,
            userId = currentUserId,
            onClickMoreMedia = { mediaType -> navController.navigate(Route.ShelfList(currentUserId, mediaType)) },
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
            onClickItem = { mediaNav -> navController.navigate(Route.Detail(mediaNav)) },
            onBack = { navController.popBackStack() },
        )
    }

    composable<Route.OtherShelf> { backStackEntry ->
        val otherShelf: Route.OtherShelf = backStackEntry.toRoute()

        OtherShelfScreen(
            viewModel = shelfViewModel,
            commonUiManager = commonUiManager,
            userId = otherShelf.userId,
            onClickMoreMedia = { mediaType -> navController.navigate(Route.ShelfList(otherShelf.userId, mediaType)) },
            onClickItem = { mediaNav -> navController.navigate(Route.Detail(mediaNav)) },
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