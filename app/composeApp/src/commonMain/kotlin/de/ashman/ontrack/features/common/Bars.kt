package de.ashman.ontrack.features.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnTrackTopBar(
    title: String,
    titleIcon: ImageVector? = null,
    navigationIcon: ImageVector? = null,
    actionIcon: ImageVector? = null,
    dropdownMenu: @Composable () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    showActionBadge: Boolean = false,
    showNavigationBadge: Boolean = false,
    onClickAction: () -> Unit = {},
    onClickNavigation: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                titleIcon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                    )
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        navigationIcon = {
            navigationIcon?.let {
                IconButton(onClickNavigation) {
                    BadgedBox(
                        badge = { if (showNavigationBadge) Badge() }
                    ) {
                        Icon(
                            imageVector = navigationIcon,
                            contentDescription = null,
                        )
                    }
                }
            }
        },
        actions = {
            actionIcon?.let {
                IconButton(onClickAction) {
                    BadgedBox(
                        badge = { if (showActionBadge) Badge() }
                    ) {
                        Icon(
                            imageVector = actionIcon,
                            contentDescription = null,
                        )
                    }
                }
            }
            dropdownMenu()
        },
        scrollBehavior = scrollBehavior,
    )
}