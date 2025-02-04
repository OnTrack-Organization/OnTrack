package de.ashman.ontrack.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navController: NavController,
    onBottomNavigation: (Route) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    var showBottomBar by rememberSaveable { mutableStateOf(true) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination

    showBottomBar = BottomNavItem.items.any { item ->
        currentRoute?.route == item.route::class.qualifiedName
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
            ) {
                BottomAppBar(
                    currentRoute = currentRoute,
                    onBottomNavigation = onBottomNavigation,
                )
            }

        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
fun BottomAppBar(
    currentRoute: NavDestination?,
    onBottomNavigation: (Route) -> Unit,
) {
    NavigationBar {
        BottomNavItem.items.forEach { route ->
            val isSelected = currentRoute?.hierarchy?.any { it.route == route.route::class.qualifiedName } == true

            NavigationBarItem(
                selected = isSelected,
                onClick = { if (!isSelected) onBottomNavigation(route.route) },
                icon = { Icon(imageVector = if (isSelected) route.filledIcon() else route.icon(), contentDescription = null) },
                label = {
                    Text(
                        text = stringResource(route.title),
                        softWrap = false,
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer),
            )
        }
    }
}
