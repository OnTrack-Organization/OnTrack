package de.ashman.ontrack.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.features.detail.DetailScreen
import de.ashman.ontrack.features.detail.DetailViewModel
import de.ashman.ontrack.features.detail.recommendation.RecommendationViewModel
import de.ashman.ontrack.features.search.SearchScreen
import de.ashman.ontrack.features.search.SearchViewModel
import de.ashman.ontrack.navigation.CustomNavType
import de.ashman.ontrack.navigation.MediaNavigationItems
import de.ashman.ontrack.navigation.Route
import de.ashman.ontrack.repository.firestore.FirestoreUserRepository
import kotlin.reflect.typeOf

fun NavGraphBuilder.searchGraph(
    navController: NavController,
    searchViewModel: SearchViewModel,
    detailViewModel: DetailViewModel,
    recommendationViewModel: RecommendationViewModel,
    commonUiManager: CommonUiManager,
    firestoreUserRepository: FirestoreUserRepository,
) {
    composable<Route.Search> {
        SearchScreen(
            viewModel = searchViewModel,
            onClickItem = { mediaNav -> navController.navigate(Route.Detail(mediaNav)) }
        )
    }

    composable<Route.Detail>(
        typeMap = mapOf(
            typeOf<MediaNavigationItems>() to CustomNavType.MediaNavigationItemsType,
        ),
        // TODO make this work
        deepLinks = listOf(
            navDeepLink { uriPattern = "ontrack://detail/{mediaNavItems}" }
        )
    ) { backStackEntry ->
        val detail: Route.Detail = backStackEntry.toRoute()

        DetailScreen(
            mediaNavItems = detail.mediaNavItems,
            detailViewModel = detailViewModel,
            recommendationViewModel = recommendationViewModel,
            commonUiManager = commonUiManager,
            onClickItem = { mediaNavItems ->
                // Remove all the Detail Navigations from graph before navigating
                navController.navigate(Route.Detail(mediaNavItems)) {
                    popUpTo<Route.Detail> {
                        inclusive = true
                    }
                }
            },
            onClickUser = { userId -> navController.navigateToShelf(userId, firestoreUserRepository) },
            onBack = { navController.popBackStack() },
        )
    }
}