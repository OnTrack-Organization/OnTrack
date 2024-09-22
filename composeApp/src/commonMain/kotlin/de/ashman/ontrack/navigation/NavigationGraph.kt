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
import de.ashman.ontrack.media.movie.ui.MovieScreen
import de.ashman.ontrack.shelf.ShelfScreen
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()

    OnTrackScreen(navController) { padding ->
        NavHost(
            navController = navController,
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

                MovieScreen(
                    id = movie.id,
                    onBack = { navController.popBackStack() }
                )
            }

            mainGraph(modifier = Modifier.fillMaxSize().padding(padding), navController)
        }
    }
}

fun NavGraphBuilder.mediaGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {

}

fun NavGraphBuilder.mainGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    composable<Route.Home> {
        HomeScreen(
            modifier = modifier,
            goToDetail = { id -> navController.navigate(Route.Movie(id)) }
        )
    }

    composable<Route.Feed> {
        FeedScreen(
            modifier = modifier,
        )
    }

    composable<Route.Shelf> {
        ShelfScreen(
            modifier = modifier
        )
    }
}
