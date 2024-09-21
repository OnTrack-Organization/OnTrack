package de.ashman.ontrack.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import de.ashman.ontrack.common.FeedScreen
import de.ashman.ontrack.common.HomeScreen
import de.ashman.ontrack.login.ui.LoginScreen
import de.ashman.ontrack.media.movie.ui.MovieScreen
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (Firebase.auth.currentUser != null) Home else Login,
    ) {
        composable<Login> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Home)
                }
            )
        }

        composable<Home> {
            HomeScreen(
                onClickNavItem = { route -> navController.navigate(route) },
                goToDetail = { id -> navController.navigate(Movie(id)) },
            )
        }

        composable<Feed> {
            FeedScreen()
        }

        composable<Movie> { backStackEntry ->
            val movie: Movie = backStackEntry.toRoute()

            MovieScreen(
                id = movie.id,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
