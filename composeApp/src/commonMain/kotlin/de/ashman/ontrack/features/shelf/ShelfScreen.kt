package de.ashman.ontrack.features.shelf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
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
import ontrack.composeapp.generated.resources.shelf_empty
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
                expandedHeight = 110.dp,
                scrollBehavior = if (uiState.trackings.isEmpty()) null else scrollBehavior,
            )
        }
    ) { contentPadding ->
        if (uiState.trackings.isEmpty()) {
            EmptyShelfContent()
        } else {
            LazyColumn(
                modifier = Modifier.padding(
                    start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = contentPadding.calculateTopPadding(),
                    end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
                    bottom = 0.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                state = viewModel.listState,
            ) {
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
fun ShelfItem(
    mediaType: MediaType,
    items: List<Tracking>?,
    onClickMore: (MediaType) -> Unit,
    onClickItem: (MediaNavigationItems) -> Unit,
) {
    items?.let {
        Column {
            Row(
                modifier = Modifier.padding(start = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = mediaType.getMediaTypeUi().icon,
                    contentDescription = "Media Icon",
                )
                Text(
                    text = pluralStringResource(mediaType.getMediaTypeUi().title, 2),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.weight(1f))

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
            text = stringResource(Res.string.shelf_empty),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}