package de.ashman.ontrack.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import de.ashman.ontrack.domain.user.User
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
            onNavigateAfterLogin = { userExists, user ->
                navController.navigate(if (userExists) Route.Search else Route.Setup(user)) {
                    popUpTo(Route.Login) { inclusive = true }
                }
            },
            onBack = { navController.popBackStack() }
        )
    }

    composable<Route.Setup>(
        typeMap = mapOf(
            typeOf<User?>() to CustomNavType.UserType,
        )
    ) { backStackEntry ->
        val setup: Route.Setup = backStackEntry.toRoute()

        SetupScreen(
            viewModel = setupViewModel,
            user = setup.user,
            onGoToApp = {
                navController.navigate(Route.Search) {
                    popUpTo<Route.Setup> { inclusive = true }
                }
            }
        )
    }
}