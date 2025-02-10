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
import de.ashman.ontrack.authentication.AuthService
import de.ashman.ontrack.authentication.AuthViewModel
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.features.detail.DetailScreen
import de.ashman.ontrack.features.detail.DetailViewModel
import de.ashman.ontrack.features.feed.FeedScreen
import de.ashman.ontrack.features.feed.FeedViewModel
import de.ashman.ontrack.features.init.intro.IntroScreen
import de.ashman.ontrack.features.init.login.LoginScreen
import de.ashman.ontrack.features.init.start.StartScreen
import de.ashman.ontrack.features.init.start.StartViewModel
import de.ashman.ontrack.features.search.SearchScreen
import de.ashman.ontrack.features.search.SearchViewModel
import de.ashman.ontrack.features.shelf.OtherUserShelf
import de.ashman.ontrack.features.shelf.ShelfScreen
import de.ashman.ontrack.features.shelf.ShelfViewModel
import de.ashman.ontrack.features.shelflist.ShelfListScreen
import de.ashman.ontrack.features.shelflist.ShelfListViewModel
import de.ashman.ontrack.navigation.Route.Detail
import de.ashman.ontrack.navigation.Route.OtherShelf
import de.ashman.ontrack.navigation.Route.ShelfList
import org.koin.compose.koinInject
import kotlin.reflect.typeOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationGraph(
    navController: NavHostController = rememberNavController(),
    startViewModel: StartViewModel = koinInject(),
    authViewModel: AuthViewModel = koinInject(),
    feedViewModel: FeedViewModel = koinInject(),
    searchViewModel: SearchViewModel = koinInject(),
    detailViewModel: DetailViewModel = koinInject(),
    shelfViewModel: ShelfViewModel = koinInject(),
    shelfListViewModel: ShelfListViewModel = koinInject(),
    authService: AuthService = koinInject(),
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
            startDestination = if (authService.currentUserId != null) Route.Feed else Route.Start,
        ) {
            initGraph(
                startViewModel = startViewModel,
                authViewModel = authViewModel,
                navController = navController
            )
            mainGraph(
                modifier = Modifier.fillMaxSize().padding(padding),
                navController = navController,
                feedViewModel = feedViewModel,
                searchViewModel = searchViewModel,
                shelfViewModel = shelfViewModel,
                authService = authService,
            )
            mediaGraph(
                detailViewModel = detailViewModel,
                shelfListViewModel = shelfListViewModel,
                shelfViewModel = shelfViewModel,
                navController = navController
            )
        }
    }
}

fun NavGraphBuilder.initGraph(
    startViewModel: StartViewModel,
    authViewModel: AuthViewModel,
    navController: NavHostController,
) {
    composable<Route.Start> {
        StartScreen(
            viewModel = startViewModel,
            onGoToIntro = { navController.navigate(Route.Intro) },
            onGoToLogin = { navController.navigate(Route.Login) },
        )
    }

    composable<Route.Intro> {
        IntroScreen(
            onGoToLogin = { navController.navigate(Route.Login) },
        )
    }

    composable<Route.Login> {
        LoginScreen(
            viewModel = authViewModel,
            onNavigateAfterLogin = {
                navController.navigate(Route.Search) {
                    popUpTo(Route.Login) { inclusive = true }
                }
            }
        )
    }
}

fun NavGraphBuilder.mainGraph(
    modifier: Modifier,
    navController: NavHostController,
    feedViewModel: FeedViewModel,
    searchViewModel: SearchViewModel,
    shelfViewModel: ShelfViewModel,
    authService: AuthService,
) {
    composable<Route.Feed> {
        FeedScreen(
            modifier = modifier,
            viewModel = feedViewModel,
            onFriendsClick = {
                // TODO Maybe change not to route, but bottom sheet content?
                //navController.navigate(Route.Friends)
            },
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
            userId = authService.currentUserId,
            onClickMore = { mediaType -> navController.navigate(ShelfList(mediaType)) },
            onClickItem = { item -> navController.navigate(Detail(item)) },
        )
    }
}

fun NavGraphBuilder.mediaGraph(
    detailViewModel: DetailViewModel,
    shelfListViewModel: ShelfListViewModel,
    shelfViewModel: ShelfViewModel,
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

    // TODO other users shelf. use the same vm or another one? also pass the user id when navigating there
    composable<OtherShelf> { backStackEntry ->
        val otherShelf: OtherShelf = backStackEntry.toRoute()

        OtherUserShelf(
            viewModel = shelfViewModel,
            userId = otherShelf.userId,
            onClickMore = { mediaType -> navController.navigate(ShelfList(mediaType)) },
            onClickItem = { item -> navController.navigate(Detail(item)) },
            onBack = { navController.popBackStack() },
        )
    }
}
