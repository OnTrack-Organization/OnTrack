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
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.features.detail.DetailScreen
import de.ashman.ontrack.features.detail.DetailViewModel
import de.ashman.ontrack.features.feed.FeedScreen
import de.ashman.ontrack.user.ui.LoginScreen
import de.ashman.ontrack.navigation.Route.Detail
import de.ashman.ontrack.features.search.SearchScreen
import de.ashman.ontrack.features.search.SearchViewModel
import de.ashman.ontrack.features.shelf.ShelfListScreen
import de.ashman.ontrack.features.shelf.ShelfScreen
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.compose.koinInject
import kotlin.reflect.typeOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationGraph() {
    val navController = rememberNavController()
    // TODO maybe put somewhere else, but for now it needs to be here so its not recreated on every navigation
    val searchViewModel: SearchViewModel = koinInject()
    val detailViewModel: DetailViewModel = koinInject()

    val searchUiState by searchViewModel.uiState.collectAsState()

    OnTrackScreen(
        navController = navController,
        icon = searchUiState.selectedMediaType.icon
    ) { padding ->
        NavHost(
            navController = navController,
            modifier = Modifier.fillMaxSize().padding(padding),
            startDestination = if (Firebase.auth.currentUser != null) Route.Search else Route.Login,
        ) {
            composable<Route.Login> {
                LoginScreen(
                    onLoginSuccess = { navController.navigate(Route.Feed) }
                )
            }
            mainGraph(navController, searchViewModel)

            mediaGraph(detailViewModel)

            shelfGraph(navController)
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

fun NavGraphBuilder.shelfGraph(
    navController: NavHostController,
) {
    composable<Route.ShelfList> { backStackEntry ->
        val shelfList: Route.ShelfList = backStackEntry.toRoute()

        val mediaTypeEnum = MediaType.valueOf(shelfList.mediaType.uppercase())
        //TODO die liste implementieren
        ShelfListScreen(mediaTypeEnum)
    }
}

fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
    searchViewModel: SearchViewModel,
) {
    composable<Route.Feed> {
        FeedScreen()
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
