package de.ashman.ontrack.features.detail.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.tracking.TrackStatus
import de.ashman.ontrack.features.common.DEFAULT_POSTER_HEIGHT
import de.ashman.ontrack.features.common.MediaPoster
import de.ashman.ontrack.features.common.OnTrackButton
import de.ashman.ontrack.features.common.OnTrackOutlinedIconButton
import de.ashman.ontrack.features.common.SMALL_POSTER_HEIGHT
import de.ashman.ontrack.features.tracking.getColor
import de.ashman.ontrack.features.tracking.getIcon
import de.ashman.ontrack.features.tracking.getLabel
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.no_title
import ontrack.composeapp.generated.resources.track_button
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StickyMainContent(
    imageModifier: Modifier = Modifier,
    media: Media?,
    mediaType: MediaType,
    mediaTitle: String? = null,
    mediaCoverUrl: String? = null,
    status: TrackStatus? = null,
    scrollBehavior: TopAppBarScrollBehavior,
    onClickAddTracking: () -> Unit,
    onClickRemoveTracking: () -> Unit,
) {
    var mainInfoItems by remember { mutableStateOf(emptyList<String>()) }

    LaunchedEffect(media) {
        media?.let {
            mainInfoItems = it.getMainInfoItems()
        }
    }

    val transition = updateTransition(targetState = scrollBehavior.state.contentOffset < 0, label = "Image Size Transition")
    val size by transition.animateDp { isScrolledDown ->
        if (isScrolledDown) SMALL_POSTER_HEIGHT else DEFAULT_POSTER_HEIGHT
    }

    Column(
        modifier = Modifier.padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MediaPoster(
            modifier = imageModifier.height(size),
            coverUrl = mediaCoverUrl,
        )

        Column {
            MediaTitle(
                title = mediaTitle,
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
                modifier = Modifier.weight(1f).animateContentSize(),
                text = if (status != null) status.getLabel(mediaType) else Res.string.track_button,
                icon = if (status != null) status.getIcon(true) else Icons.Default.Add,
                color = status.getColor(),
                onClick = onClickAddTracking,
            )
            AnimatedVisibility(
                visible = status != null,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally(),
            ) {
                OnTrackOutlinedIconButton(
                    icon = Icons.Outlined.Delete,
                    color = status.getColor(),
                    onClick = onClickRemoveTracking
                )
            }
        }
    }
}


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
fun MediaTitle(
    title: String?,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    title?.let {
        Text(
            modifier = modifier,
            text = if (title.isBlank()) stringResource(Res.string.no_title) else title,
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