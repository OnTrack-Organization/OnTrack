package de.ashman.ontrack.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import de.ashman.ontrack.OnTrackScreen
import de.ashman.ontrack.feed.FeedScreen
import de.ashman.ontrack.home.HomeScreen
import de.ashman.ontrack.login.ui.LoginScreen
import de.ashman.ontrack.media.ui.detail.MovieDetailScreen
import de.ashman.ontrack.shelf.MediaType
import de.ashman.ontrack.shelf.ShelfListScreen
import de.ashman.ontrack.shelf.ShelfScreen
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()

    OnTrackScreen(navController) { padding ->
        NavHost(
            navController = navController,
            modifier = Modifier.fillMaxSize().padding(padding),
            startDestination = if (Firebase.auth.currentUser != null) Route.Home else Route.Login,
        ) {
            composable<Route.Login> {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Route.Home)
                    }
                )
            }

            composable<Route.Movie> { backStackEntry ->
                val movie: Route.Movie = backStackEntry.toRoute()

                MovieDetailScreen(
                    id = movie.id,
                    onBack = { navController.popBackStack() }
                )
            }

            mainGraph(navController)
            shelfGraph(navController)
        }
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
) {
    composable<Route.Home> {
        HomeScreen(
            goToMovieDetail = { id -> navController.navigate(Route.Movie(id)) }
        )
    }

    composable<Route.Feed> {
        FeedScreen()
    }

    composable<Route.Shelf> {
        ShelfScreen(
            goToShelf = { mediaType -> navController.navigate(Route.ShelfList(mediaType.name)) }
        )
    }
}
