package de.ashman.ontrack

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import de.ashman.ontrack.navigation.BottomNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnTrackScreen(
    navController: NavController,
    icon: @Composable () -> ImageVector,
    content: @Composable (PaddingValues) -> Unit,
) {
    val bottomBarState = rememberSaveable { mutableStateOf(true) }
    val topBarState = rememberSaveable { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination

    bottomBarState.value = BottomNavItem.items.any { item ->
        currentRoute?.route == item.route::class.qualifiedName
    }

    val detailBaseRoute = "de.ashman.ontrack.navigation.Route.Detail"
    topBarState.value = currentRoute?.hierarchy?.any { it.route?.contains(detailBaseRoute) == true } == true

    // TODO fix animation
    Scaffold(
        topBar = {
            /*AnimatedVisibility(
                visible = topBarState.value,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it }),
            ) {
                DetailTopBar(navController, icon)
            }*/
            if (topBarState.value) DetailTopBar(navController, icon)
        },
        bottomBar = {
            /*AnimatedVisibility(
                visible = bottomBarState.value,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ) {
                BottomAppBar(navController, currentRoute)
            }*/
            if (bottomBarState.value) BottomAppBar(navController, currentRoute)
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
fun BottomAppBar(
    navController: NavController,
    currentRoute: NavDestination?,
) {
    NavigationBar {
        BottomNavItem.items.forEach { route ->
            val isSelected = currentRoute?.hierarchy?.any { it.route == route.route::class.qualifiedName } == true

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) navController.navigate(route.route)
                },
                icon = { if (isSelected) route.filledIcon() else route.icon() },
                label = {
                    Text(
                        text = route.title,
                        softWrap = false,
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopBar(
    navController: NavController,
    icon: @Composable () -> ImageVector
) {
    CenterAlignedTopAppBar(
        title = {
            Icon(
                imageVector = icon(),
                contentDescription = "Media Type Icon"
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back Icon")
            }
        }
    )
}
