package de.ashman.ontrack.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import de.ashman.ontrack.domain.user.NewUser
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.features.init.intro.IntroScreen
import de.ashman.ontrack.features.init.login.LoginScreen
import de.ashman.ontrack.features.init.login.LoginViewModel
import de.ashman.ontrack.features.init.setup.SetupScreen
import de.ashman.ontrack.features.init.setup.SetupViewModel
import de.ashman.ontrack.features.init.start.StartScreen
import de.ashman.ontrack.features.init.start.StartViewModel
import de.ashman.ontrack.navigation.CustomNavType
import de.ashman.ontrack.navigation.Route
import kotlin.reflect.typeOf

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    startViewModel: StartViewModel,
    loginViewModel: LoginViewModel,
    setupViewModel: SetupViewModel,
    commonUiManager: CommonUiManager,
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
            commonUiManager = commonUiManager,
            onNavigateToSearch = {
                navController.navigate(Route.Search) {
                    popUpTo(Route.Login) { inclusive = true }
                }
            },
            onNavigateToSetup = { newUser ->
                navController.navigate(Route.Setup(newUser)) {
                    popUpTo(Route.Login) { inclusive = true }
                }
            },
            onBack = { navController.popBackStack() }
        )
    }

    composable<Route.Setup>(
        typeMap = mapOf(
            typeOf<NewUser>() to CustomNavType.NewUserType,
        )
    ) { backStackEntry ->
        val setup: Route.Setup = backStackEntry.toRoute()

        SetupScreen(
            viewModel = setupViewModel,
            commonUiManager = commonUiManager,
            user = setup.user,
            navigateToSearch = {
                navController.navigate(Route.Search) {
                    popUpTo<Route.Setup> { inclusive = true }
                }
            }
        )
    }
}