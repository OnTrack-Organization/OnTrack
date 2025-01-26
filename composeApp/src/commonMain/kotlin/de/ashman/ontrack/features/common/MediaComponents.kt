package de.ashman.ontrack.features.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HideSource
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import de.ashman.ontrack.domain.Media
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_description
import ontrack.composeapp.generated.resources.not_available
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainInfo(
    mainInfoItems: List<String>,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        mainInfoItems.forEachIndexed { index, item ->
            Text(
                text = item,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier
                    .defaultMinSize(minWidth = 0.dp)
                    .wrapContentWidth(Alignment.Start)
                    .widthIn(max = 200.dp)
            )

            if (index < mainInfoItems.size - 1) {
                VerticalDivider(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    thickness = 2.dp,
                    modifier = Modifier
                        .height(16.dp)
                        .padding(horizontal = 8.dp),
                )
            }
        }
    }
}

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
    Column(
        modifier = Modifier.width(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SubcomposeAsyncImage(
            model = coverUrl,
            contentScale = ContentScale.Crop,
            contentDescription = "Cover",
            modifier = modifier
                .aspectRatio(2f / 3f)
                .clip(shape = MaterialTheme.shapes.medium)
                .let { if (onClick != null) it.clickable { onClick() } else it }
        ) {
            val state = painter.state.collectAsState().value

            when (state) {
                is AsyncImagePainter.State.Loading -> {
                    Card {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(modifier = Modifier.scale(1.5f))
                        }
                    }
                }

                is AsyncImagePainter.State.Error -> {
                    Card {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                modifier = Modifier.size(50.dp),
                                imageVector = Icons.Default.HideSource,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                contentDescription = "No Results Icon"
                            )
                        }
                    }
                }

                else -> {
                    SubcomposeAsyncImageContent()
                    TrackOverlay(
                        trackStatusIcon = trackStatusIcon,
                        trackStatusRating = trackStatusRating,
                    )
                }
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
                modifier = Modifier.size(36.dp).alpha(0.8F),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier.size(36.dp),
                    imageVector = Icons.Default.Star,
                    contentDescription = "Status Icon",
                    tint = Color.White,
                )
                Text(
                    text = "$trackStatusRating",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black,
                )
            }
        }

        trackStatusIcon?.let {
            Icon(
                imageVector = trackStatusIcon,
                contentDescription = trackStatusIcon.name,
                modifier = Modifier.size(32.dp).alpha(0.8F),
                tint = Color.White,
            )
        }
    }
}

@Composable
fun MediaTitle(
    title: String?,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    title?.let {
        Text(
            modifier = modifier,
            text = if (title.isBlank()) stringResource(Res.string.not_available) else title,
            style = textStyle.copy(
                hyphens = Hyphens.Auto,
                lineBreak = LineBreak.Heading,
            ),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            softWrap = true
        )
    }
}

@Composable
fun MediaRow(
    title: String,
    items: List<Media>?,
    onClickItem: (Media) -> Unit = { },
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
                items(items) {
                    MediaPoster(
                        modifier = Modifier.height(SMALL_POSTER_HEIGHT),
                        title = it.title,
                        coverUrl = it.coverUrl,
                        textStyle = MaterialTheme.typography.titleSmall,
                        onClick = { onClickItem(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun MediaDescription(
    description: String?,
    modifier: Modifier = Modifier,
) {
    description?.let {
        Column(
            modifier = modifier.padding(horizontal = 16.dp),
        ) {
            Text(
                text = stringResource(Res.string.detail_description),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(description)
        }
    }
}

@Composable
fun MediaChips(
    title: String,
    items: List<String>?,
    modifier: Modifier = Modifier,
) {
    items?.let {
        Column(
            modifier = modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(items) {
                    OutlinedCard(
                        modifier = Modifier.width(IntrinsicSize.Max),
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                            text = it,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }
        }
    }
}
