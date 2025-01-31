package de.ashman.ontrack

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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import de.ashman.ontrack.navigation.BottomNavItem
import de.ashman.ontrack.navigation.Route
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnTrackScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    icon: ImageVector,
    onBack: () -> Unit,
    onBottomNavigation: (Route) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    var bottomBarState by rememberSaveable { mutableStateOf(true) }
    var topBarState by rememberSaveable { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(state = TopAppBarState(-Float.MAX_VALUE, 0F, 0F))

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination

    bottomBarState = BottomNavItem.items.any { item ->
        currentRoute?.route == item.route::class.qualifiedName
    }

    val detailBaseRoute = "de.ashman.ontrack.navigation.Route.Detail"
    topBarState = currentRoute?.hierarchy?.any { it.route?.contains(detailBaseRoute) == true } == true

    Scaffold(
        modifier = if (topBarState) Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) else Modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (topBarState) DetailTopBar(
                icon = icon,
                scrollBehavior = scrollBehavior,
                onBack = onBack,
            )
        },
        bottomBar = {
            if (bottomBarState) BottomAppBar(
                currentRoute = currentRoute,
                onBottomNavigation = onBottomNavigation,
            )
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
                onClick = {
                    if (!isSelected) onBottomNavigation(route.route)
                },
                icon = {
                    Icon(imageVector = if (isSelected) route.filledIcon() else route.icon(), contentDescription = null)
                },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopBar(
    icon: ImageVector,
    scrollBehavior: TopAppBarScrollBehavior,
    onBack: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            Icon(
                imageVector = icon,
                contentDescription = "Media Type Icon"
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back Icon")
            }
        },
        scrollBehavior = scrollBehavior,
    )
}
