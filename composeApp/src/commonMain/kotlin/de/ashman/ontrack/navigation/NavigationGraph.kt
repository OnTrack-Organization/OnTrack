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
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.features.detail.DetailScreen
import de.ashman.ontrack.features.detail.DetailViewModel
import de.ashman.ontrack.features.feed.FeedScreen
import de.ashman.ontrack.features.feed.FeedViewModel
import de.ashman.ontrack.features.feed.friend.FriendsViewModel
import de.ashman.ontrack.features.init.intro.IntroScreen
import de.ashman.ontrack.features.init.login.LoginScreen
import de.ashman.ontrack.features.init.start.StartScreen
import de.ashman.ontrack.features.init.start.StartViewModel
import de.ashman.ontrack.features.search.SearchScreen
import de.ashman.ontrack.features.search.SearchViewModel
import de.ashman.ontrack.features.settings.SettingsScreen
import de.ashman.ontrack.features.settings.SettingsViewModel
import de.ashman.ontrack.features.shelf.OtherUserShelf
import de.ashman.ontrack.features.shelf.ShelfScreen
import de.ashman.ontrack.features.shelf.ShelfViewModel
import de.ashman.ontrack.features.shelflist.ShelfListScreen
import de.ashman.ontrack.features.shelflist.ShelfListViewModel
import dev.gitlive.firebase.analytics.Event
import dev.gitlive.firebase.analytics.FirebaseAnalytics
import org.koin.compose.koinInject
import kotlin.reflect.typeOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationGraph(
    navController: NavHostController = rememberNavController(),
    startViewModel: StartViewModel = koinInject(),
    authViewModel: AuthViewModel = koinInject(),
    feedViewModel: FeedViewModel = koinInject(),
    friendsViewModel: FriendsViewModel = koinInject(),
    searchViewModel: SearchViewModel = koinInject(),
    detailViewModel: DetailViewModel = koinInject(),
    shelfViewModel: ShelfViewModel = koinInject(),
    shelfListViewModel: ShelfListViewModel = koinInject(),
    settingsViewModel: SettingsViewModel = koinInject(),
    authService: AuthService = koinInject(),
    analytics: FirebaseAnalytics = koinInject(),
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
            startDestination = if (authService.currentUserId.isNotBlank()) Route.Search else Route.Start,
        ) {
            initGraph(
                startViewModel = startViewModel,
                authViewModel = authViewModel,
                navController = navController,
                analytics = analytics,
            )
            mainGraph(
                modifier = Modifier.fillMaxSize().padding(padding),
                navController = navController,
                feedViewModel = feedViewModel,
                friendsViewModel = friendsViewModel,
                searchViewModel = searchViewModel,
                shelfViewModel = shelfViewModel,
                authService = authService,
            )
            mediaGraph(
                detailViewModel = detailViewModel,
                shelfListViewModel = shelfListViewModel,
                shelfViewModel = shelfViewModel,
                authService = authService,
                navController = navController
            )

            composable<Route.Settings> {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onBack = { navController.popBackStack() },
                    userId = authService.currentUserId,
                    onLogout = {
                        friendsViewModel.clearViewModel()
                        feedViewModel.clearViewModel()
                        detailViewModel.clearViewModel()
                        shelfListViewModel.clearViewModel()
                        shelfViewModel.clearViewModel()
                        searchViewModel.clearViewModel()
                        settingsViewModel.clearViewModel()
                        authViewModel.clearViewModel()

                        authViewModel.signOut()

                        navController.navigate(Route.Start) {
                            popUpTo(Route.Feed) { inclusive = true } // Clear backstack
                        }
                    }
                )
            }
        }
    }
}

fun NavGraphBuilder.initGraph(
    startViewModel: StartViewModel,
    authViewModel: AuthViewModel,
    navController: NavHostController,
    analytics: FirebaseAnalytics,
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
            onBack = { navController.popBackStack() }
        )
    }

    composable<Route.Login> {
        LoginScreen(
            viewModel = authViewModel,
            onNavigateAfterLogin = {
                analytics.logEvent(analytics.Event.LOGIN)

                navController.navigate(Route.Search) {
                    popUpTo(Route.Login) { inclusive = true }
                }
            },
            onBack = { navController.popBackStack() }
        )
    }
}

fun NavGraphBuilder.mainGraph(
    modifier: Modifier,
    navController: NavHostController,
    feedViewModel: FeedViewModel,
    friendsViewModel: FriendsViewModel,
    searchViewModel: SearchViewModel,
    shelfViewModel: ShelfViewModel,
    authService: AuthService,
) {
    composable<Route.Feed> {
        FeedScreen(
            feedViewModel = feedViewModel,
            friendsViewModel = friendsViewModel,
            onClickCover = { mediaNav ->
                navController.navigate(Route.Detail(mediaNav))
            },
            onUserClick = { userId ->
                navController.navigate(if (userId == authService.currentUserId) Route.Shelf else Route.OtherShelf(userId))
            },
        )
    }

    composable<Route.Search> {
        SearchScreen(
            modifier = modifier,
            viewModel = searchViewModel,
            authService = authService,
            onClickItem = { mediaNav -> navController.navigate(Route.Detail(mediaNav)) }
        )
    }

    composable<Route.Shelf> {
        ShelfScreen(
            viewModel = shelfViewModel,
            userId = authService.currentUserId,
            onClickMore = { mediaType -> navController.navigate(Route.ShelfList(authService.currentUserId, mediaType)) },
            onClickItem = { mediaNav -> navController.navigate(Route.Detail(mediaNav)) },
            onSettings = { navController.navigate(Route.Settings) },
        )
    }
}

fun NavGraphBuilder.mediaGraph(
    detailViewModel: DetailViewModel,
    shelfListViewModel: ShelfListViewModel,
    shelfViewModel: ShelfViewModel,
    authService: AuthService,
    navController: NavHostController,
) {
    composable<Route.Detail>(
        typeMap = mapOf(
            typeOf<MediaNavigationItems>() to CustomNavType.MediaNavigationItemsType,
        )
    ) { backStackEntry ->
        val detail: Route.Detail = backStackEntry.toRoute()

        DetailScreen(
            mediaNavItems = detail.mediaNavItems,
            viewModel = detailViewModel,
            onClickItem = { mediaNavItems ->
                // Remove all the Detail Navigations from graph before navigating
                navController.navigate(Route.Detail(mediaNavItems)) {
                    popUpTo<Route.Detail> {
                        inclusive = true
                    }
                }
            },
            onUserClick = { userId ->
                navController.navigate(if (userId == authService.currentUserId) Route.Shelf else Route.OtherShelf(userId))
            },
            onBack = {
                navController.popBackStack()
            },
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
            onBack = {
                navController.popBackStack()
            },
        )
    }

    composable<Route.OtherShelf> { backStackEntry ->
        val otherShelf: Route.OtherShelf = backStackEntry.toRoute()

        OtherUserShelf(
            viewModel = shelfViewModel,
            userId = otherShelf.userId,
            onClickMore = { mediaType -> navController.navigate(Route.ShelfList(otherShelf.userId, mediaType)) },
            onClickItem = { mediaNavItem -> navController.navigate(Route.Detail(mediaNavItem)) },
            onBack = { navController.popBackStack() },
        )
    }
}
