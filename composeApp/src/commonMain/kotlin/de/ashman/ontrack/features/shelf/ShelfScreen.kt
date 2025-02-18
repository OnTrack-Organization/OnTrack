package de.ashman.ontrack.features.shelf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.features.common.DEFAULT_POSTER_HEIGHT
import de.ashman.ontrack.features.common.MediaPoster
import de.ashman.ontrack.features.common.PersonImage
import de.ashman.ontrack.navigation.BottomNavItem
import de.ashman.ontrack.navigation.MediaNavigationItems
import de.ashman.ontrack.util.getMediaTypeUi
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.shelf_own_empty
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelfScreen(
    viewModel: ShelfViewModel,
    userId: String,
    onClickMore: (MediaType) -> Unit,
    onClickItem: (MediaNavigationItems) -> Unit,
    onBack: (() -> Unit)? = null,
    onSettings: (() -> Unit)? = null,
    emptyText: StringResource = Res.string.shelf_own_empty,
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(userId) {
        viewModel.observeUser(userId)
        viewModel.observeUserTrackings(userId)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        uiState.user?.let {
                            AccountComponent(
                                name = it.name,
                                accountName = it.username,
                                imageUrl = it.imageUrl,
                            )
                        }
                    },
                    navigationIcon = {
                        onBack?.let {
                            IconButton(
                                onClick = onBack,
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                    contentDescription = "Back",
                                )
                            }
                        }
                    },
                    actions = {
                        onSettings?.let {
                            IconButton(
                                onClick = it,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                )
                            }
                        }
                    },
                    expandedHeight = 110.dp,
                    scrollBehavior = if (uiState.trackings.isEmpty()) null else scrollBehavior,
                )
            }
        }
    ) { contentPadding ->
        if (uiState.trackings.isEmpty()) {
            EmptyShelfContent(text = emptyText)
        } else {
            LazyColumn(
                modifier = Modifier.padding(contentPadding).padding(bottom = if (onSettings != null) 80.dp else 0.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                state = viewModel.listState,
            ) {
                item { MediaCounts(trackings = uiState.trackings) }

                MediaType.entries.forEach { mediaType ->
                    val filteredTrackings = uiState.trackings.filter { it.mediaType == mediaType }

                    if (filteredTrackings.isNotEmpty()) {
                        item(key = mediaType.name) {
                            ShelfItem(
                                mediaType = mediaType,
                                items = filteredTrackings,
                                onClickMore = { onClickMore(mediaType) },
                                onClickItem = onClickItem,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AccountComponent(
    name: String?,
    accountName: String?,
    imageUrl: String?,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PersonImage(
            modifier = Modifier.size(42.dp),
            userImageUrl = imageUrl,
        )

        name?.let {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }
        accountName?.let {
            Text(
                text = accountName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun MediaCounts(
    trackings: List<Tracking>,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MediaType.entries.forEach { mediaType ->
                val count = trackings.count { it.mediaType == mediaType }

                MediaCount(
                    icon = mediaType.getMediaTypeUi().outlinedIcon,
                    count = count
                )
            }
        }
        HorizontalDivider()
    }
}

@Composable
fun MediaCount(
    icon: ImageVector,
    count: Int,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(42.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.fillMaxSize().padding(4.dp),
                imageVector = icon,
                contentDescription = "Media Icon",
            )
        }
        Text(
            text = "$count",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun ShelfItem(
    mediaType: MediaType,
    items: List<Tracking>?,
    onClickMore: (MediaType) -> Unit,
    onClickItem: (MediaNavigationItems) -> Unit,
) {
    items?.let {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.padding(start = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.size(42.dp))

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier.size(42.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            modifier = Modifier.fillMaxSize().padding(4.dp),
                            imageVector = mediaType.getMediaTypeUi().outlinedIcon,
                            contentDescription = "Media Icon",
                        )
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = pluralStringResource(mediaType.getMediaTypeUi().title, 2),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

                IconButton(
                    onClick = { onClickMore(mediaType) },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                        contentDescription = "Show more",
                    )
                }
            }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                items(items.take(5)) { item ->
                    MediaPoster(
                        modifier = Modifier.height(DEFAULT_POSTER_HEIGHT),
                        coverUrl = item.mediaCoverUrl,
                        onClick = { onClickItem(MediaNavigationItems(item.mediaId, item.mediaTitle, item.mediaCoverUrl, item.mediaType)) },
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyShelfContent(
    modifier: Modifier = Modifier,
    text: StringResource,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(48.dp),
            imageVector = BottomNavItem.ShelfNav.filledIcon(),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            contentDescription = null
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = stringResource(text),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
