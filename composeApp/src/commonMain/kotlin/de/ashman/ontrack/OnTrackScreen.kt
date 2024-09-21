package de.ashman.ontrack

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.sp
import de.ashman.ontrack.navigation.BottomNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnTrackScreen(
    onClickNavItem: (Any) -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val selectedRoute = remember { mutableStateOf(BottomNavItem.items.first()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(selectedRoute.value.title)
                },
                navigationIcon = {
                    IconButton(
                        // TODO open burger menu sidebar or similar
                        onClick = { },
                        content = { Icon(Icons.Filled.Menu, "Burger Menu Icon") }
                    )
                },
                actions = {}
            )
        },
        bottomBar = {
            NavigationBar {
                BottomNavItem.items.forEach { navItem ->
                    NavigationBarItem(
                        selected = selectedRoute.value == navItem,
                        onClick = {
                            onClickNavItem(navItem.route)

                            selectedRoute.value = navItem
                        },
                        icon = {
                            Icon(
                                imageVector = navItem.icon,
                                contentDescription = navItem.title,
                            )
                        },
                        label = {
                            Text(
                                text = navItem.title,
                                fontSize = 10.sp,
                                softWrap = false,
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer),
                    )
                }
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}
