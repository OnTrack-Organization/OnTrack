package de.ashman.ontrack.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import de.ashman.ontrack.common.FeedScreen
import de.ashman.ontrack.common.HomeScreen

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Home,
    ) {
        composable<Home> {
            HomeScreen(
                onClickNavItem = { route -> navController.navigate(route) },
                goToDetail = { /*id -> navController.navigate(Detail(id))*/ },
            )
        }
        composable<Feed> { backStackEntry ->
            //val feed: Feed = backStackEntry.toRoute()

            FeedScreen()
        }
    }
}
