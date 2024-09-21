package de.ashman.ontrack.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val title: String,
    val route: Any,
    val icon: ImageVector
) {
    object HomeNav : BottomNavItem("Home", Home, Icons.Default.Home)
    object FeedNav : BottomNavItem("Feed", Feed, Icons.Default.Forum)

    companion object {
        val items: List<BottomNavItem> = listOf(HomeNav, FeedNav)
    }
}