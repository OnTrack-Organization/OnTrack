package de.ashman.ontrack.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.shelves_filled
import org.jetbrains.compose.resources.vectorResource

sealed class BottomNavItem(
    val title: String,
    val route: Route,
    val icon: @Composable () -> Unit,
) {
    data object HomeNav : BottomNavItem("Home", Route.Home, { Icon(Icons.Default.Home, "Home Icon") })
    data object SearchNav : BottomNavItem("Search", Route.Search, { Icon(Icons.Default.Search, "Search Icon") })
    data object ShelfNav : BottomNavItem("Shelf", Route.Shelf, { Icon(vectorResource(Res.drawable.shelves_filled), "Shelf Icon") })

    data object FeedNav : BottomNavItem("Feed", Route.Feed, { Icon(Icons.Default.Forum, "Feed Icon") })

    companion object {
        val items: List<BottomNavItem> = listOf(HomeNav, SearchNav, ShelfNav)
    }
}