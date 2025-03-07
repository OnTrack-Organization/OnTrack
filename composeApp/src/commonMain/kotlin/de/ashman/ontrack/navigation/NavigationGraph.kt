package de.ashman.ontrack.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import co.touchlab.kermit.Logger
import com.mmk.kmpnotifier.notification.NotifierManager
import de.ashman.ontrack.db.AuthRepository
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.features.detail.DetailScreen
import de.ashman.ontrack.features.detail.DetailViewModel
import de.ashman.ontrack.features.feed.FeedScreen
import de.ashman.ontrack.features.feed.FeedViewModel
import de.ashman.ontrack.features.feed.friend.FriendsViewModel
import de.ashman.ontrack.features.init.intro.IntroScreen
import de.ashman.ontrack.features.init.login.LoginScreen
import de.ashman.ontrack.features.init.login.LoginViewModel
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
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.Event
import dev.gitlive.firebase.analytics.FirebaseAnalytics
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import kotlin.reflect.typeOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    startViewModel: StartViewModel = koinInject(),
    loginViewModel: LoginViewModel = koinInject(),
    feedViewModel: FeedViewModel = koinInject(),
    friendsViewModel: FriendsViewModel = koinInject(),
    searchViewModel: SearchViewModel = koinInject(),
    detailViewModel: DetailViewModel = koinInject(),
    shelfViewModel: ShelfViewModel = koinInject(),
    shelfListViewModel: ShelfListViewModel = koinInject(),
    settingsViewModel: SettingsViewModel = koinInject(),
    authRepository: AuthRepository = koinInject(),
    analytics: FirebaseAnalytics = koinInject(),
) {
    val coroutineScope = rememberCoroutineScope()

    MainScaffold(
        navController = navController,
        onBottomNavigation = { route ->
            navController.navigate(route) {
                // Clear backstack so that pressing back in main screens closes the app
                popUpTo(0) {}
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (authRepository.currentUserId.isNotBlank()) Route.Search else Route.Start,
        ) {
            initGraph(
                startViewModel = startViewModel,
                loginViewModel = loginViewModel,
                navController = navController,
                analytics = analytics,
            )
            mainGraph(
                navController = navController,
                feedViewModel = feedViewModel,
                friendsViewModel = friendsViewModel,
                searchViewModel = searchViewModel,
                shelfViewModel = shelfViewModel,
                authRepository = authRepository,
            )
            mediaGraph(
                detailViewModel = detailViewModel,
                shelfListViewModel = shelfListViewModel,
                shelfViewModel = shelfViewModel,
                authRepository = authRepository,
                navController = navController
            )

            composable<Route.Settings> {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onBack = { navController.popBackStack() },
                    userId = authRepository.currentUserId,
                    onLogout = {
                        friendsViewModel.clearViewModel()
                        feedViewModel.clearViewModel()
                        detailViewModel.clearViewModel()
                        shelfListViewModel.clearViewModel()
                        shelfViewModel.clearViewModel()
                        searchViewModel.clearViewModel()
                        settingsViewModel.clearViewModel()
                        loginViewModel.clearViewModel()

                        navController.navigate(Route.Start) {
                            popUpTo(Route.Feed) { inclusive = true } // Clear backstack
                        }
                    }
                )
            }
        }
    }

    // Listener for token changes
    NotifierManager.addListener(object : NotifierManager.Listener {
        override fun onNewToken(token: String) {
            Logger.i("Push Notification onNewToken: $token")
            if (Firebase.auth.currentUser == null) return

            coroutineScope.launch {
                authRepository.updateFcmToken(token)
            }
        }
    })
}

fun NavGraphBuilder.initGraph(
    startViewModel: StartViewModel,
    loginViewModel: LoginViewModel,
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
            viewModel = loginViewModel,
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
    navController: NavHostController,
    feedViewModel: FeedViewModel,
    friendsViewModel: FriendsViewModel,
    searchViewModel: SearchViewModel,
    shelfViewModel: ShelfViewModel,
    authRepository: AuthRepository,
) {
    composable<Route.Feed> {
        FeedScreen(
            feedViewModel = feedViewModel,
            friendsViewModel = friendsViewModel,
            onClickCover = { mediaNav ->
                navController.navigate(Route.Detail(mediaNav))
            },
            onUserClick = { userId ->
                navController.navigate(if (userId == authRepository.currentUserId) Route.Shelf else Route.OtherShelf(userId))
            },
        )
    }

    composable<Route.Search> {
        SearchScreen(
            viewModel = searchViewModel,
            onClickItem = { mediaNav -> navController.navigate(Route.Detail(mediaNav)) }
        )
    }

    composable<Route.Shelf> {
        ShelfScreen(
            viewModel = shelfViewModel,
            userId = authRepository.currentUserId,
            onClickMore = { mediaType -> navController.navigate(Route.ShelfList(authRepository.currentUserId, mediaType)) },
            onClickItem = { mediaNav -> navController.navigate(Route.Detail(mediaNav)) },
            onSettings = { navController.navigate(Route.Settings) },
        )
    }
}

fun NavGraphBuilder.mediaGraph(
    detailViewModel: DetailViewModel,
    shelfListViewModel: ShelfListViewModel,
    shelfViewModel: ShelfViewModel,
    authRepository: AuthRepository,
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
                navController.navigate(if (userId == authRepository.currentUserId) Route.Shelf else Route.OtherShelf(userId))
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
