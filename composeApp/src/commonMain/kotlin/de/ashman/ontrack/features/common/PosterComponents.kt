package de.ashman.ontrack.features.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HideSource
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import de.ashman.ontrack.domain.Franchise
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.Season
import de.ashman.ontrack.features.detail.components.MediaTitle
import de.ashman.ontrack.navigation.MediaNavigationItems

@Composable
fun MediaPoster(
    modifier: Modifier = Modifier,
    title: String? = null,
    coverUrl: String?,
    trackStatusIcon: ImageVector? = null,
    trackStatusRating: Int? = null,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    onClick: (() -> Unit)? = null,
) {
    val painter = rememberAsyncImagePainter(coverUrl)
    val imageState = painter.state.collectAsState().value

    Column(
        modifier = Modifier.width(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Surface(
            modifier = modifier
                .aspectRatio(2f / 3f),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant,
            enabled = (onClick != null),
            onClick = { onClick?.invoke() },
        ) {
            when (imageState) {
                is AsyncImagePainter.State.Loading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is AsyncImagePainter.State.Success -> {
                    Image(
                        painter = painter,
                        contentScale = ContentScale.Crop,
                        contentDescription = "Poster Image",
                    )
                    TrackOverlay(
                        trackStatusIcon = trackStatusIcon,
                        trackStatusRating = trackStatusRating,
                    )
                }

                is AsyncImagePainter.State.Error -> {
                    Box(
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            modifier = Modifier.fillMaxSize(0.5f),
                            imageVector = Icons.Default.HideSource,
                            contentDescription = "No Image",
                        )
                    }
                }

                else -> {}
            }
        }

        MediaTitle(
            title = title,
            textStyle = textStyle,
        )
    }
}

@Composable
fun TrackOverlay(
    trackStatusIcon: ImageVector?,
    trackStatusRating: Int?,
) {
    Column(
        modifier = Modifier.padding(4.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End,
    ) {
        trackStatusRating?.let {
            Box(
                modifier = Modifier.size(36.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier.size(36.dp),
                    imageVector = Icons.Default.Star,
                    contentDescription = "Status Icon",
                )
                Text(
                    text = "$trackStatusRating",
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }

        trackStatusIcon?.let {
            Icon(
                imageVector = trackStatusIcon,
                contentDescription = trackStatusIcon.name,
                modifier = Modifier.size(32.dp),
                tint = Color.White,
            )
        }
    }
}

@Composable
fun MediaPosterRow(
    title: String,
    items: List<Any>?,
    onClickItem: (MediaNavigationItems) -> Unit = { },
) {
    items?.let {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                items(items) { item ->
                    when (item) {
                        is Media -> {
                            MediaPoster(
                                modifier = Modifier.height(SMALL_POSTER_HEIGHT),
                                title = item.title,
                                coverUrl = item.coverUrl,
                                textStyle = MaterialTheme.typography.titleSmall,
                                onClick = {
                                    onClickItem(
                                        MediaNavigationItems(item.id, item.title, item.coverUrl, item.mediaType)
                                    )
                                },
                            )
                        }

                        is Franchise -> {
                            MediaPoster(
                                modifier = Modifier.height(SMALL_POSTER_HEIGHT),
                                title = item.name,
                                coverUrl = item.imageUrl,
                                textStyle = MaterialTheme.typography.titleSmall,
                            )
                        }

                        is Season -> {
                            MediaPoster(
                                modifier = Modifier.height(SMALL_POSTER_HEIGHT),
                                title = item.title,
                                coverUrl = item.coverUrl,
                                textStyle = MaterialTheme.typography.titleSmall,
                            )
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}
