package de.ashman.ontrack.features.shelf

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.features.common.MediaPoster
import de.ashman.ontrack.features.common.SMALL_POSTER_HEIGHT
import de.ashman.ontrack.util.getMediaTypeUi
import org.jetbrains.compose.resources.pluralStringResource
import kotlin.collections.filter

@Composable
fun ShelfScreen(
    viewModel: ShelfViewModel,
    onClickMore: (MediaType) -> Unit,
    onClickItem: (Media) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    Column {
        uiState.user?.let {
            ProfileRow(
                name = it.name,
                accountName = it.username,
                imageUrl = it.imageUrl,
            )
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            state = listState,
        ) {
            val nonEmptySections = MediaType.entries.filter { mediaType ->
                uiState.mediaList.any { it.mediaType == mediaType }
            }

            nonEmptySections.forEachIndexed { index, mediaType ->
                val filteredItems = uiState.mediaList.filter { it.mediaType == mediaType }
                item(key = mediaType.name) {
                    ShelfItem(
                        mediaType = mediaType,
                        items = filteredItems,
                        onClickMore = { onClickMore(mediaType) },
                        onClickItem = onClickItem,
                    )
                    /*if (index < nonEmptySections.lastIndex) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 32.dp))
                    }*/
                }
            }
        }
    }
}

@Composable
fun ProfileRow(
    name: String?,
    accountName: String?,
    imageUrl: String?,
) {
    val painter = rememberAsyncImagePainter(imageUrl)

    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(54.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            val state = painter.state.collectAsState().value

            when (state) {
                is AsyncImagePainter.State.Loading -> {
                    CircularProgressIndicator()
                }

                is AsyncImagePainter.State.Success -> {
                    Image(
                        painter = painter,
                        contentScale = ContentScale.Crop,
                        contentDescription = "Account Image",
                    )
                }

                is AsyncImagePainter.State.Error -> {
                    Icon(
                        modifier = Modifier.padding(8.dp),
                        imageVector = Icons.Default.Person,
                        contentDescription = "No Image",
                    )
                }

                else -> {}
            }
        }

        Column(
            verticalArrangement = Arrangement.Center,
        ) {
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
}

@Composable
fun ShelfItem(
    mediaType: MediaType,
    items: List<Media>?,
    onClickMore: (MediaType) -> Unit,
    onClickItem: (Media) -> Unit,
) {
    items?.let {
        Column {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
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
                contentPadding = PaddingValues(16.dp),
            ) {
                items(items.take(10)) { item ->
                    MediaPoster(
                        modifier = Modifier.height(SMALL_POSTER_HEIGHT),
                        coverUrl = item.coverUrl,
                        onClick = { onClickItem(item) },
                    )
                }
            }
        }
    }
}