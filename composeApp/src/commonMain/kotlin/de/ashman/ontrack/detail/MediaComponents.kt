package de.ashman.ontrack.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HideSource
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import de.ashman.ontrack.media.model.Media
import de.ashman.ontrack.util.DEFAULT_POSTER_HEIGHT
import de.ashman.ontrack.util.DEFAULT_POSTER_WIDTH
import de.ashman.ontrack.util.PosterSize
import de.ashman.ontrack.util.SMALL_POSTER_HEIGHT
import de.ashman.ontrack.util.SMALL_POSTER_WIDTH
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_description
import ontrack.composeapp.generated.resources.detail_genres
import org.jetbrains.compose.resources.stringResource

@Composable
fun MediaPoster(
    title: String,
    coverUrl: String?,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    posterSize: PosterSize = PosterSize.DEFAULT,
    isTextOverflow: Boolean = false,
    onClickItem: () -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SubcomposeAsyncImage(
            model = coverUrl,
            contentScale = ContentScale.Crop,
            contentDescription = "Cover",
            modifier = Modifier
                .size(width = posterSize.width, height = posterSize.height)
                .clip(shape = RoundedCornerShape(16.dp))
                .clickable {
                    onClickItem()
                }
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
                    SubcomposeAsyncImageContent(
                        modifier = Modifier
                    )
                }
            }
        }

        Text(
            modifier = if (isTextOverflow) Modifier.fillMaxWidth() else Modifier.width(posterSize.width),
            text = if (title.isEmpty()) "N/A" else title,
            style = textStyle,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun MediaRow(
    title: String,
    otherMedia: List<Media>?,
    onClickItem: (String) -> Unit = { },
) {
    if (otherMedia != null) {
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
                items(otherMedia) {
                    MediaPoster(
                        title = it.name,
                        coverUrl = it.coverUrl,
                        textStyle = MaterialTheme.typography.titleSmall,
                        posterSize = PosterSize.SMALL,
                        onClickItem = { onClickItem(it.id) }
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
    if (description == null) return

    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(Res.string.detail_description),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(description)
    }
}

@Composable
fun MediaGenres(
    genres: List<String>?,
    modifier: Modifier = Modifier,
) {
    if (genres == null) return

    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(Res.string.detail_genres),
            style = MaterialTheme.typography.titleMedium,
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(genres) {
                AssistChip(
                    onClick = {},
                    label = { Text(it) },
                )
            }
        }
    }
}
