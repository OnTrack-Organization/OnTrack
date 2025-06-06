package de.ashman.ontrack.navigation.graph

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import de.ashman.ontrack.datastore.UserDataStore
import de.ashman.ontrack.navigation.Route

fun NavGraphBuilder.splashGraph(
    navController: NavController,
    userDataStore: UserDataStore,
) {
    composable<Route.Splash> {
        LaunchedEffect(Unit) {
            userDataStore.currentUser.collect { currentUser ->
                if (currentUser != null && currentUser.username.isNotBlank()) {
                    navController.navigate(Route.Search) {
                        popUpTo(0)
                    }
                } else {
                    navController.navigate(Route.Start) {
                        popUpTo(0)
                    }
                }
                return@collect
            }
        }

        // TODO add clean splash screen here
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
