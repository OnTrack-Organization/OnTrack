package de.ashman.ontrack.navigation.graph

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import de.ashman.ontrack.datastore.UserDataStore
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.features.detail.DetailViewModel
import de.ashman.ontrack.features.friend.FriendsViewModel
import de.ashman.ontrack.features.init.setup.SetupViewModel
import de.ashman.ontrack.features.init.signin.LoginViewModel
import de.ashman.ontrack.features.init.start.StartViewModel
import de.ashman.ontrack.features.notification.NotificationViewModel
import de.ashman.ontrack.features.search.SearchViewModel
import de.ashman.ontrack.features.settings.SettingsViewModel
import de.ashman.ontrack.features.share.PostViewModel
import de.ashman.ontrack.features.shelf.ShelfViewModel
import de.ashman.ontrack.features.shelflist.ShelfListViewModel
import de.ashman.ontrack.navigation.MainScaffold
import de.ashman.ontrack.navigation.Route
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    startViewModel: StartViewModel = koinInject(),
    loginViewModel: LoginViewModel = koinInject(),
    postViewModel: PostViewModel = koinInject(),
    friendsViewModel: FriendsViewModel = koinInject(),
    searchViewModel: SearchViewModel = koinInject(),
    detailViewModel: DetailViewModel = koinInject(),
    shelfViewModel: ShelfViewModel = koinInject(),
    shelfListViewModel: ShelfListViewModel = koinInject(),
    settingsViewModel: SettingsViewModel = koinInject(),
    setupViewModel: SetupViewModel = koinInject(),
    notificationViewModel: NotificationViewModel = koinInject(),
    commonUiManager: CommonUiManager = koinInject(),
    userDataStore: UserDataStore = koinInject(),
) {
    val currentUser = userDataStore.currentUser.collectAsState(initial = null).value
    val startDestination = if (currentUser != null && currentUser.username.isNotBlank()) Route.Search else Route.Start

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
            startDestination = startDestination,
        ) {
            authGraph(
                navController = navController,
                startViewModel = startViewModel,
                loginViewModel = loginViewModel,
                setupViewModel = setupViewModel,
                commonUiManager = commonUiManager,
            )

            shareGraph(
                navController = navController,
                postViewModel = postViewModel,
                friendsViewModel = friendsViewModel,
                notificationViewModel = notificationViewModel,
                commonUiManager = commonUiManager,
            )

            searchGraph(
                navController = navController,
                searchViewModel = searchViewModel,
                detailViewModel = detailViewModel,
                commonUiManager = commonUiManager,
            )

            currentUser?.let {
                shelfGraph(
                    currentUserId = it.id,
                    navController = navController,
                    shelfViewModel = shelfViewModel,
                    shelfListViewModel = shelfListViewModel,
                    settingsViewModel = settingsViewModel,
                    commonUiManager = commonUiManager,
                    clearViewModels = {
                        // TODO different way
                        friendsViewModel.clearViewModel()
                        postViewModel.clearViewModel()
                        detailViewModel.clearViewModel()
                        shelfListViewModel.clearViewModel()
                        shelfViewModel.clearViewModel()
                        settingsViewModel.clearViewModel()
                        loginViewModel.clearViewModel()
                        setupViewModel.clearViewModel()
                        notificationViewModel.clearViewModel()
                    }
                )
            }
        }
    }
}

fun NavController.navigateToShelf(userId: String) {
    // TODO change
    val route = if (userId == Firebase.auth.currentUser?.uid) {
        Route.Shelf
    } else {
        Route.OtherShelf(userId)
    }
    navigate(route)
}
