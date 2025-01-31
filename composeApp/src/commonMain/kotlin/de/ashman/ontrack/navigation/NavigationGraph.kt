package de.ashman.ontrack.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import de.ashman.ontrack.OnTrackScreen
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
import de.ashman.ontrack.navigation.Route.Detail
import de.ashman.ontrack.util.getMediaTypeUi
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.compose.koinInject
import kotlin.reflect.typeOf

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> { error("No Snackbar Host State") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationGraph() {
    val navController = rememberNavController()

    val searchViewModel: SearchViewModel = koinInject()
    val detailViewModel: DetailViewModel = koinInject()
    val shelfViewModel: ShelfViewModel = koinInject()
    val authViewModel: AuthViewModel = koinInject()

    val searchUiState by searchViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
        OnTrackScreen(
            navController = navController,
            snackbarHostState = snackbarHostState,
            onBack = { navController.popBackStack() },
            onBottomNavigation = { route -> navController.navigate(route) },
            icon = searchUiState.selectedMediaType.getMediaTypeUi().icon,
        ) { padding ->
            NavHost(
                navController = navController,
                modifier = Modifier.fillMaxSize().padding(padding),
                startDestination = if (Firebase.auth.currentUser != null) Route.Shelf else Route.Login,
            ) {
                composable<Route.Login> {
                    LoginScreen(
                        authViewModel = authViewModel,
                        onLoginSuccess = { navController.navigate(Route.Feed) }
                    )
                }
                mainGraph(navController, searchViewModel, authViewModel, shelfViewModel)
                mediaGraph(detailViewModel, navController)
            }
        }
    }
}

fun NavGraphBuilder.mediaGraph(
    detailViewModel: DetailViewModel,
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
                navController.navigate(Detail(item))
            },
        )
    }
}

fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
    searchViewModel: SearchViewModel,
    authViewModel: AuthViewModel,
    shelfViewModel: ShelfViewModel,
) {
    composable<Route.Feed> {
        FeedScreen(
            authViewModel = authViewModel,
            onClickLogout = { navController.navigate(Route.Login) },
        )
    }

    composable<Route.Search> {
        SearchScreen(
            viewModel = searchViewModel,
            onClickItem = { item ->
                navController.navigate(Detail(item))
            }
        )
    }

    composable<Route.Shelf> {
        ShelfScreen(
            viewModel = shelfViewModel,
            onClickMore = { mediaType ->
                // TODO navigate to shelf list
                //navController.navigate()
            },
            onClickItem = { item ->
                navController.navigate(Detail(item))
            }
        )
    }
}
