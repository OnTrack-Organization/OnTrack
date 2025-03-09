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
import de.ashman.ontrack.repository.firestore.FirestoreUserRepository
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.features.detail.DetailScreen
import de.ashman.ontrack.features.detail.DetailViewModel
import de.ashman.ontrack.features.detail.recommendation.RecommendationViewModel
import de.ashman.ontrack.features.feed.FeedScreen
import de.ashman.ontrack.features.feed.FeedViewModel
import de.ashman.ontrack.features.feed.friend.FriendsViewModel
import de.ashman.ontrack.features.init.intro.IntroScreen
import de.ashman.ontrack.features.init.login.LoginScreen
import de.ashman.ontrack.features.init.login.LoginViewModel
import de.ashman.ontrack.features.init.setup.SetupScreen
import de.ashman.ontrack.features.init.setup.SetupViewModel
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
    recommendationViewModel: RecommendationViewModel = koinInject(),
    shelfViewModel: ShelfViewModel = koinInject(),
    shelfListViewModel: ShelfListViewModel = koinInject(),
    settingsViewModel: SettingsViewModel = koinInject(),
    setupViewModel: SetupViewModel = koinInject(),
    firestoreUserRepository: FirestoreUserRepository = koinInject(),
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
            startDestination = if (firestoreUserRepository.currentUserId.isNotBlank()) Route.Search else Route.Start,
        ) {
            initGraph(
                startViewModel = startViewModel,
                loginViewModel = loginViewModel,
                navController = navController,
                setupViewModel = setupViewModel,
                analytics = analytics,
            )
            mainGraph(
                navController = navController,
                feedViewModel = feedViewModel,
                friendsViewModel = friendsViewModel,
                searchViewModel = searchViewModel,
                shelfViewModel = shelfViewModel,
                firestoreUserRepository = firestoreUserRepository,
            )
            mediaGraph(
                detailViewModel = detailViewModel,
                recommendationViewModel = recommendationViewModel,
                shelfListViewModel = shelfListViewModel,
                shelfViewModel = shelfViewModel,
                firestoreUserRepository = firestoreUserRepository,
                navController = navController
            )

            composable<Route.Settings> {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onBack = { navController.popBackStack() },
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
                firestoreUserRepository.updateFcmToken(token)
            }
        }
    })
}

fun NavGraphBuilder.initGraph(
    startViewModel: StartViewModel,
    loginViewModel: LoginViewModel,
    setupViewModel: SetupViewModel,
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
            onNavigateAfterLogin = { userExists, user ->
                analytics.logEvent(analytics.Event.LOGIN)

                navController.navigate(if (userExists) Route.Search else Route.Setup(user)) {
                    popUpTo(Route.Login) { inclusive = true }
                }
            },
            onBack = { navController.popBackStack() }
        )
    }

    composable<Route.Setup>(
        typeMap = mapOf(
            typeOf<User?>() to CustomNavType.UserType,
        )
    ) { backStackEntry ->
        val setup: Route.Setup = backStackEntry.toRoute()

        SetupScreen(
            viewModel = setupViewModel,
            user = setup.user,
            onGoToApp = {
                navController.navigate(Route.Search) {
                    popUpTo<Route.Setup> { inclusive = true }
                }
            }
        )
    }
}

fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
    feedViewModel: FeedViewModel,
    friendsViewModel: FriendsViewModel,
    searchViewModel: SearchViewModel,
    shelfViewModel: ShelfViewModel,
    firestoreUserRepository: FirestoreUserRepository,
) {
    composable<Route.Feed> {
        FeedScreen(
            feedViewModel = feedViewModel,
            friendsViewModel = friendsViewModel,
            onClickCover = { mediaNav ->
                navController.navigate(Route.Detail(mediaNav))
            },
            onUserClick = { userId ->
                navController.navigate(if (userId == firestoreUserRepository.currentUserId) Route.Shelf else Route.OtherShelf(userId))
            },
        )
    }

    composable<Route.Search> {
        SearchScreen(
            userId = Firebase.auth.currentUser?.uid.orEmpty(),
            viewModel = searchViewModel,
            onClickItem = { mediaNav -> navController.navigate(Route.Detail(mediaNav)) }
        )
    }

    composable<Route.Shelf> {
        ShelfScreen(
            viewModel = shelfViewModel,
            userId = firestoreUserRepository.currentUserId,
            onClickMore = { mediaType -> navController.navigate(Route.ShelfList(firestoreUserRepository.currentUserId, mediaType)) },
            onClickItem = { mediaNav -> navController.navigate(Route.Detail(mediaNav)) },
            onSettings = { navController.navigate(Route.Settings) },
        )
    }
}

fun NavGraphBuilder.mediaGraph(
    detailViewModel: DetailViewModel,
    recommendationViewModel: RecommendationViewModel,
    shelfListViewModel: ShelfListViewModel,
    shelfViewModel: ShelfViewModel,
    firestoreUserRepository: FirestoreUserRepository,
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
            detailViewModel = detailViewModel,
            recommendationViewModel = recommendationViewModel,
            onClickItem = { mediaNavItems ->
                // Remove all the Detail Navigations from graph before navigating
                navController.navigate(Route.Detail(mediaNavItems)) {
                    popUpTo<Route.Detail> {
                        inclusive = true
                    }
                }
            },
            onClickUser = { userId ->
                navController.navigate(if (userId == firestoreUserRepository.currentUserId) Route.Shelf else Route.OtherShelf(userId))
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
