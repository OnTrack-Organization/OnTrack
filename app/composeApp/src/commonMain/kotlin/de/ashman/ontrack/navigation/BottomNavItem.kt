package de.ashman.ontrack.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SavedSearch
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.search_nav_title
import ontrack.composeapp.generated.resources.share_nav_title
import ontrack.composeapp.generated.resources.shelf_nav_title
import ontrack.composeapp.generated.resources.shelves_filled
import ontrack.composeapp.generated.resources.shelves_outlined
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.vectorResource

sealed class BottomNavItem(
    val title: StringResource,
    val route: Route,
    val icon: @Composable () -> ImageVector,
    val filledIcon: @Composable () -> ImageVector,
) {
    data object ShareNav : BottomNavItem(
        title = Res.string.share_nav_title,
        route = Route.Share,
        icon = { Icons.Outlined.Public },
        filledIcon = { Icons.Filled.Public }
    )

    data object SearchNav : BottomNavItem(
        title = Res.string.search_nav_title,
        route = Route.Search,
        icon = { Icons.Outlined.Search },
        filledIcon = { Icons.Filled.SavedSearch }
    )

    data object ShelfNav : BottomNavItem(
        title = Res.string.shelf_nav_title,
        route = Route.Shelf,
        icon = { vectorResource(Res.drawable.shelves_outlined) },
        filledIcon = { vectorResource(Res.drawable.shelves_filled) }
    )

    companion object {
        val items: Set<BottomNavItem> = setOf(ShareNav, SearchNav, ShelfNav)
    }
}