package de.ashman.ontrack.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import de.ashman.ontrack.util.SMALL_POSTER_HEIGHT
import de.ashman.ontrack.util.SMALL_POSTER_WIDTH
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_description
import ontrack.composeapp.generated.resources.detail_genres
import ontrack.composeapp.generated.resources.detail_similar
import ontrack.composeapp.generated.resources.media_movies
import org.jetbrains.compose.resources.stringResource

@Composable
fun MediaPoster(
    name: String,
    coverUrl: String?,
    onClickItem: () -> Unit = {},
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    width: Dp = DEFAULT_POSTER_WIDTH,
    height: Dp = DEFAULT_POSTER_HEIGHT,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.width(width)
    ) {
        SubcomposeAsyncImage(
            model = coverUrl,
            contentScale = ContentScale.Crop,
            contentDescription = "Cover",
            modifier = modifier
                .size(width = width, height = height)
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
            modifier = Modifier.fillMaxWidth(),
            text = if (name.isEmpty()) "N/A" else name,
            style = textStyle,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun MediaDescription(
    description: String?,
) {
    if (description == null) return

    Column(
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
) {
    if (genres == null) return

    Column(
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

@Composable
fun SimilarMedia(
    similarMedia: List<Media>?,
    onClickItem: (Media) -> Unit = { },
) {
    if (similarMedia != null) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(Res.string.detail_similar, stringResource(Res.string.media_movies)),
                style = MaterialTheme.typography.titleMedium,
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                items(similarMedia) {
                    MediaPoster(
                        name = it.name,
                        coverUrl = it.coverUrl,
                        textStyle = MaterialTheme.typography.titleSmall,
                        width = SMALL_POSTER_WIDTH,
                        height = SMALL_POSTER_HEIGHT,
                        onClickItem = { onClickItem(it) },
                    )
                }
            }
        }
    }
}