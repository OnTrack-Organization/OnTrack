package de.ashman.ontrack.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import de.ashman.ontrack.authentication.AuthViewModel
import de.ashman.ontrack.authentication.LoginScreen
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.features.detail.DetailScreen
import de.ashman.ontrack.features.detail.DetailViewModel
import de.ashman.ontrack.features.feed.FeedScreen
import de.ashman.ontrack.features.search.SearchScreen
import de.ashman.ontrack.features.search.SearchViewModel
import de.ashman.ontrack.features.shelf.ShelfScreen
import de.ashman.ontrack.features.shelf.ShelfViewModel
import de.ashman.ontrack.features.shelflist.ShelfListScreen
import de.ashman.ontrack.features.shelflist.ShelfListViewModel
import de.ashman.ontrack.navigation.Route.Detail
import de.ashman.ontrack.navigation.Route.ShelfList
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.compose.koinInject
import kotlin.reflect.typeOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = koinInject(),
    searchViewModel: SearchViewModel = koinInject(),
    detailViewModel: DetailViewModel = koinInject(),
    shelfViewModel: ShelfViewModel = koinInject(),
    shelfListViewModel: ShelfListViewModel = koinInject(),
) {
    MainScaffold(
        navController = navController,
        onBottomNavigation = { route ->
            navController.navigate(route) {
                // Clear backstack so that pressing back in main screens closes the app
                popUpTo(0) {}
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = if (Firebase.auth.currentUser != null) Route.Shelf else Route.Login,
        ) {
            loginGraph(
                authViewModel = authViewModel,
                navController = navController
            )
            mainGraph(
                modifier = Modifier.fillMaxSize().padding(padding),
                navController = navController,
                searchViewModel = searchViewModel,
                authViewModel = authViewModel,
                shelfViewModel = shelfViewModel
            )
            mediaGraph(
                detailViewModel = detailViewModel,
                shelfListViewModel = shelfListViewModel,
                navController = navController
            )
        }
    }
}

fun NavGraphBuilder.loginGraph(
    authViewModel: AuthViewModel,
    navController: NavHostController,
) {
    composable<Route.Login> {
        LoginScreen(
            authViewModel = authViewModel,
            onLoginSuccess = {
                navController.navigate(Route.Feed) {
                    popUpTo(Route.Login) { inclusive = true }
                }
            }
        )
    }
}

fun NavGraphBuilder.mainGraph(
    modifier: Modifier,
    navController: NavHostController,
    searchViewModel: SearchViewModel,
    authViewModel: AuthViewModel,
    shelfViewModel: ShelfViewModel,
) {
    composable<Route.Feed> {
        FeedScreen(
            modifier = modifier,
            authViewModel = authViewModel,
            onClickLogout = { navController.navigate(Route.Login) },
        )
    }

    composable<Route.Search> {
        SearchScreen(
            modifier = modifier,
            viewModel = searchViewModel,
            onClickItem = { item -> navController.navigate(Detail(item)) }
        )
    }

    composable<Route.Shelf> {
        ShelfScreen(
            modifier = modifier,
            viewModel = shelfViewModel,
            onClickMore = { mediaType -> navController.navigate(ShelfList(mediaType)) },
            onClickItem = { item -> navController.navigate(Detail(item)) }
        )
    }
}

fun NavGraphBuilder.mediaGraph(
    detailViewModel: DetailViewModel,
    shelfListViewModel: ShelfListViewModel,
    navController: NavHostController,
) {
    composable<Detail>(
        typeMap = mapOf(typeOf<Media>() to CustomNavType.MediaNavType)
    ) { backStackEntry ->
        val detail: Detail = backStackEntry.toRoute()

        DetailScreen(
            media = detail.media,
            viewModel = detailViewModel,
            onClickItem = { item ->
                // Remove all the Detail Navigations from graph before navigating
                navController.navigate(Detail(item)) {
                    popUpTo<Detail> {
                        inclusive = true
                    }
                }
            },
            onBack = {
                navController.popBackStack()
            },
        )
    }

    composable<ShelfList> { backStackEntry ->
        val shelfList: ShelfList = backStackEntry.toRoute()

        ShelfListScreen(
            viewModel = shelfListViewModel,
            mediaType = shelfList.mediaType,
            onClickItem = { item -> navController.navigate(Detail(item)) },
            onBack = { navController.popBackStack() },
        )
    }
}
