package de.ashman.ontrack.features.detail.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.HideSource
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.util.SMALL_POSTER_HEIGHT
import kotlinx.coroutines.launch
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_description
import ontrack.composeapp.generated.resources.detail_genres
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainInfo(
    mainInfoItems: List<String>,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        mainInfoItems.forEachIndexed { index, item ->
            Text(
                text = item,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    onClickItem: (() -> Unit)? = null,
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
                .clip(shape = RoundedCornerShape(16.dp))
                .let { if (onClickItem != null) it.clickable { onClickItem() } else it }
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

        MediaTitle(
            title = title,
            textStyle = textStyle,
        )
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
            text = if (title.isEmpty()) "N/A" else title,
            style = textStyle,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun MediaRow(
    title: String,
    otherMedia: List<Media>?,
    onClickItem: (String) -> Unit = { },
) {
    otherMedia?.let {
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
                        modifier = Modifier.height(SMALL_POSTER_HEIGHT),
                        title = it.title,
                        coverUrl = it.coverUrl,
                        textStyle = MaterialTheme.typography.titleSmall,
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
fun MediaGenres(
    genres: List<String>?,
    modifier: Modifier = Modifier,
) {
    genres?.let {
        Column(
            modifier = modifier.padding(horizontal = 16.dp),
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StarRating(
    modifier: Modifier = Modifier,
    rating: Float?,
    onRatingChanged: (Float) -> Unit
) {
    var selectedRating by remember { mutableStateOf(rating ?: 0F) }
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    val starWidth = 40.dp.toPx()
                    val draggedStars = (dragAmount / starWidth).coerceIn(-0.5f, 0.5f)
                    selectedRating = (selectedRating + draggedStars).coerceIn(0f, 5f)
                    onRatingChanged(selectedRating)
                }
            },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        repeat(5) { index ->
            val starRating = (index + 1).toFloat()
            val animatedScale = remember { Animatable(1f) }
            val isHalfStar = (selectedRating - index) in 0.5f..1f

            LaunchedEffect(selectedRating) {
                val scale = if (selectedRating >= starRating) 1.2f else 1f
                animatedScale.animateTo(scale)
            }

            CompositionLocalProvider(
                LocalRippleConfiguration provides null
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    onClick = {
                        coroutineScope.launch {
                            animatedScale.animateTo(1.2f)
                            animatedScale.animateTo(1f)
                        }
                        selectedRating = starRating
                        onRatingChanged(selectedRating)
                    }
                ) {
                    if (selectedRating >= starRating) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = Color(0xFFFFC700)
                        )
                    } else if (isHalfStar) {
                        Icon(
                            Icons.AutoMirrored.Filled.StarHalf,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = Color(0xFFFFC700)
                        )
                    } else {
                        Icon(
                            Icons.Filled.StarBorder,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = Color(0xFFFFC700)
                        )
                    }
                }
            }
        }
    }
}
