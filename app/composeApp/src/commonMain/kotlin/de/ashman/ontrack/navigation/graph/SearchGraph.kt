package de.ashman.ontrack.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.features.detail.DetailScreen
import de.ashman.ontrack.features.detail.DetailViewModel
import de.ashman.ontrack.features.search.SearchScreen
import de.ashman.ontrack.features.search.SearchViewModel
import de.ashman.ontrack.navigation.CustomNavType
import de.ashman.ontrack.navigation.MediaNavigationParam
import de.ashman.ontrack.navigation.Route
import kotlin.reflect.typeOf

fun NavGraphBuilder.searchGraph(
    navController: NavController,
    searchViewModel: SearchViewModel,
    detailViewModel: DetailViewModel,
    commonUiManager: CommonUiManager,
) {
    composable<Route.Search> {
        SearchScreen(
            viewModel = searchViewModel,
            onClickItem = { mediaNav -> navController.navigate(Route.Detail(mediaNav)) }
        )
    }

    composable<Route.Detail>(
        typeMap = mapOf(
            typeOf<MediaNavigationParam>() to CustomNavType.MediaNavigationParamType,
        ),
        // TODO make this work
        deepLinks = listOf(
            navDeepLink { uriPattern = "ontrack://detail/{mediaNav}" }
        )
    ) { backStackEntry ->
        val detail: Route.Detail = backStackEntry.toRoute()

        DetailScreen(
            mediaNav = detail.mediaNav,
            detailViewModel = detailViewModel,
            commonUiManager = commonUiManager,
            onClickItem = { mediaNav ->
                // Remove all the Detail Navigations from graph before navigating
                navController.navigate(Route.Detail(mediaNav)) {
                    popUpTo<Route.Detail> {
                        inclusive = true
                    }
                }
            },
            onClickUser = { userId -> navController.navigateToShelf(userId) },
            onBack = { navController.popBackStack() },
        )
    }
}