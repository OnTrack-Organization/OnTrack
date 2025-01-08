package de.ashman.ontrack

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import de.ashman.ontrack.navigation.BottomNavItem
import de.ashman.ontrack.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnTrackScreen(
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit,
) {


    Scaffold(
        topBar = {
            /*CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        // TODO open burger menu sidebar or similar
                        onClick = { },
                        content = { Icon(Icons.Filled.Menu, "Burger Menu Icon") }
                    )
                },
                actions = {}
            )*/
        },
        bottomBar = {
            BottomAppBar(navController)
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
fun BottomAppBar(
    navController: NavController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination

    val shouldShowBottomBar = BottomNavItem.items.any { item ->
        currentRoute?.hierarchy?.any { it.route == item.route::class.qualifiedName } == true
    }

    AnimatedVisibility(
        visible = shouldShowBottomBar,
        enter = slideInVertically { it },
        exit = slideOutVertically { it }

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
}