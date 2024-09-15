package de.ashman.ontrack

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.sp
import de.ashman.ontrack.navigation.BottomNavItem
import de.ashman.ontrack.navigation.Feed
import de.ashman.ontrack.navigation.Home

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnTrackScreen(
    onClickNavItem: (Any) -> Unit = {},
    topBarTitle: @Composable () -> Unit = {},
    topBarNavIcon: @Composable () -> Unit = {},
    topBarIcon: @Composable () -> Unit = {},
    showBottomBar: Boolean = true,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    topBarTitle()
                },
                navigationIcon = {
                    topBarNavIcon()
                },
                actions = {
                    topBarIcon()
                }
            )
        },
        bottomBar = {
            if (showBottomBar) {
                val selectedRoute = remember { mutableStateOf(BottomNavItem()) }

                NavigationBar {
                    bottomNavItems.forEach { navItem ->
                        NavigationBarItem(
                            selected = selectedRoute.value == navItem,
                            onClick = {
                                onClickNavItem(navItem.route)

                                selectedRoute.value = navItem
                            },
                            icon = {
                                Icon(
                                    imageVector = navItem.icon,
                                    contentDescription = navItem.name,
                                )
                            },
                            label = {
                                Text(
                                    text = navItem.name,
                                    fontSize = 10.sp,
                                    softWrap = false,
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer),
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

val bottomNavItems = listOf(
    BottomNavItem("Home", Home, Icons.Default.Home),
    BottomNavItem("Feed", Feed, Icons.Default.AccountCircle),
)