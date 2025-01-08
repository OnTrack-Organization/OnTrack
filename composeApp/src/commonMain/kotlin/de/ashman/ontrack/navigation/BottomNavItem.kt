package de.ashman.ontrack.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.shelves_filled
import ontrack.composeapp.generated.resources.shelves_outlined
import org.jetbrains.compose.resources.vectorResource

sealed class BottomNavItem(
    val title: String,
    val route: Route,
    val icon: @Composable () -> Unit,
    val filledIcon: @Composable () -> Unit,
) {
    data object FeedNav : BottomNavItem(
        title = "Feed",
        route = Route.Feed,
        icon = { Icon(Icons.Outlined.Home, "Feed Icon") },
        filledIcon = { Icon(Icons.Default.Home, "Filled Feed Icon") }
    )

    data object SearchNav : BottomNavItem(
        title = "Search",
        route = Route.Search,
        icon = { Icon(Icons.Outlined.Search, "Search Icon") },
        filledIcon = { Icon(Icons.Default.Search, "Filled Search Icon") })

    data object ShelfNav : BottomNavItem(
        title = "Shelf",
        route = Route.Shelf,
        icon = { Icon(vectorResource(Res.drawable.shelves_outlined), "Shelf Icon") },
        filledIcon = { Icon(vectorResource(Res.drawable.shelves_filled), "Filled Shelf Icon") })

    companion object {
        val items: List<BottomNavItem> = listOf(FeedNav, SearchNav, ShelfNav)
    }
}