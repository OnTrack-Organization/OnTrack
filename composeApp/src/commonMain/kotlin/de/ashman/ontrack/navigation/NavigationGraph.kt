package de.ashman.ontrack.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import de.ashman.ontrack.OnTrackScreen
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.features.detail.DetailScreen
import de.ashman.ontrack.features.detail.DetailViewModel
import de.ashman.ontrack.features.feed.FeedScreen
import de.ashman.ontrack.authentication.LoginScreen
import de.ashman.ontrack.navigation.Route.Detail
import de.ashman.ontrack.features.search.SearchScreen
import de.ashman.ontrack.features.search.SearchViewModel
import de.ashman.ontrack.features.shelf.ShelfScreen
import de.ashman.ontrack.authentication.AuthViewModel
import de.ashman.ontrack.domain.sub.getMediaTypeUi
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.compose.koinInject
import kotlin.reflect.typeOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationGraph() {
    val navController = rememberNavController()

    val searchViewModel: SearchViewModel = koinInject()
    val detailViewModel: DetailViewModel = koinInject()
    val authViewModel: AuthViewModel = koinInject()

    val searchUiState by searchViewModel.uiState.collectAsState()

    OnTrackScreen(
        navController = navController,
        icon = getMediaTypeUi(searchUiState.selectedMediaType).icon,
    ) { padding ->
        NavHost(
            navController = navController,
            modifier = Modifier.fillMaxSize().padding(padding),
            startDestination = if (Firebase.auth.currentUser != null) Route.Feed else Route.Login,
        ) {
            composable<Route.Login> {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = { navController.navigate(Route.Feed) }
                )
            }
            mainGraph(navController, searchViewModel, authViewModel)
            mediaGraph(detailViewModel)
        }
    }
}

fun NavGraphBuilder.mediaGraph(
    detailViewModel: DetailViewModel,
) {
    composable<Route.Detail>(
        typeMap = mapOf(typeOf<Media>() to CustomNavType.MediaNavType)
    ) { backStackEntry ->
        val detail: Route.Detail = backStackEntry.toRoute()

        DetailScreen(
            media = detail.media,
            viewModel = detailViewModel,
        )
    }
}

fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
    searchViewModel: SearchViewModel,
    authViewModel: AuthViewModel,
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
            goToShelf = { mediaType -> navController.navigate(Route.ShelfList(mediaType.name)) }
        )
    }
}
