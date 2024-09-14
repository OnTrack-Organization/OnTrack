package de.ashman.ontrack.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val name: String = "Home",
    val route: Any = Home,
    val icon: ImageVector = Icons.Default.Home
)