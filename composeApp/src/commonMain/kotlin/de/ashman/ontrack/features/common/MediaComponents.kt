package de.ashman.ontrack.features.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.HideSource
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import coil3.compose.rememberAsyncImagePainter
import de.ashman.ontrack.domain.MAX_RATING
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.TrackStatus
import de.ashman.ontrack.domain.TrackStatusType
import de.ashman.ontrack.features.track.getLabel
import de.ashman.ontrack.features.track.getStatusIcon
import kotlinx.coroutines.runBlocking
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.not_available
import ontrack.composeapp.generated.resources.track_button
import org.jetbrains.compose.resources.StringResource
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
    val painter = rememberAsyncImagePainter(coverUrl)

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
            val state = painter.state.collectAsState().value

            when (state) {
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
                modifier = Modifier.size(36.dp).alpha(0.8F),
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
fun MediaPosterRow(
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
    title: String,
    description: String?,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    var hasOverflow by remember { mutableStateOf(false) }

    description?.let {
        Column(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    expanded = !expanded
                },
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                )
                if (hasOverflow) {
                    Icon(imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, "Arrow")
                }
            }

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (expanded) Int.MAX_VALUE else 4,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = {
                    if (!expanded) hasOverflow = it.hasVisualOverflow
                }
            )
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
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

@Composable
fun StickyMainContent(
    imageModifier: Modifier = Modifier,
    media: Media,
    trackStatus: TrackStatusType? = null,
    onClickTrack: () -> Unit,
    onClickRemove: () -> Unit,
) {
    // TODO ugly, so change
    val mainInfoItems by remember(media) {
        mutableStateOf(runBlocking { media.getMainInfoItems() })
    }

    Column(
        modifier = Modifier.padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MediaPoster(
            modifier = imageModifier,
            coverUrl = media.coverUrl,
        )

        Column {
            MediaTitle(
                title = media.title,
                textStyle = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            )

            MainInfo(mainInfoItems = mainInfoItems)
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OnTrackButton(
                modifier = Modifier.weight(1f),
                text = if (trackStatus != null) trackStatus.getLabel(media.mediaType) else Res.string.track_button,
                icon = if (trackStatus != null) trackStatus.getStatusIcon(true) else Icons.Default.Add,
                onClick = onClickTrack,
            )
            if (trackStatus != null) {
                OnTrackIconButton(
                    icon = Icons.Outlined.Delete,
                    onClick = onClickRemove
                )
            }
        }
    }
}

@Composable
fun ReviewCard(
    trackStatus: TrackStatus,
) {
    var expanded by remember { mutableStateOf(false) }
    var hasOverflow by remember { mutableStateOf(false) }

    if (trackStatus.statusType != TrackStatusType.CATALOG && trackStatus.statusType != TrackStatusType.CONSUMING) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = MaterialTheme.shapes.medium,
            onClick = { expanded = !expanded }
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    trackStatus.statusType?.getStatusIcon(true)?.let {
                        Icon(
                            imageVector = it, it.name,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }

                    MiniStarRatingBar(rating = trackStatus.rating)

                    Spacer(modifier = Modifier.weight(1f))

                    trackStatus.timestamp?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    if (hasOverflow) {
                        Icon(imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, "Arrow")
                    }
                }

                if (!trackStatus.reviewTitle.isNullOrBlank()) {
                    Text(
                        text = trackStatus.reviewTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                    )
                }

                if (!trackStatus.reviewDescription.isNullOrBlank()) {
                    Text(
                        text = trackStatus.reviewDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = if (expanded) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = {
                            if (!expanded) hasOverflow = it.hasVisualOverflow
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MiniStarRatingBar(
    rating: Int?,
) {
    Row {
        for (i in 1..MAX_RATING) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = if (rating != null && i <= rating) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }
    }
}

@Composable
fun CreatorCard(
    title: StringResource,
    name: String?,
    subInfo: String? = null,
    description: String? = null,
    imageUrl: String?,
) {
    var expanded by remember { mutableStateOf(false) }
    val painter = rememberAsyncImagePainter(imageUrl)

    name?.let {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    expanded = !expanded
                },
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.titleMedium,
                )
                description?.let {
                    Icon(imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, "Arrow")
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier.size(42.dp),
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
                                contentDescription = "Creator Image",
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
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    subInfo?.let {
                        Text(
                            text = subInfo,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            description?.let {
                if (expanded) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
