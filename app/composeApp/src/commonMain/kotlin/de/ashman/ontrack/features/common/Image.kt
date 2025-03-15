package de.ashman.ontrack.features.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HideSource
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.valentinilk.shimmer.shimmer
import de.ashman.ontrack.domain.media.Franchise
import de.ashman.ontrack.domain.media.Media
import de.ashman.ontrack.domain.media.Season
import de.ashman.ontrack.features.detail.components.MediaTitle
import de.ashman.ontrack.features.settings.ImageUploadState
import de.ashman.ontrack.navigation.MediaNavigationItems
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch

@Composable
fun MediaPoster(
    modifier: Modifier = Modifier,
    title: String? = null,
    coverUrl: String?,
    trackStatusIcon: ImageVector? = null,
    trackStatusRating: Double? = null,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    isLoadingShimmer: Boolean = false,
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
            color = MaterialTheme.colorScheme.surface,
            enabled = (onClick != null && !isLoadingShimmer),
            onClick = { onClick?.invoke() },
        ) {
            if (isLoadingShimmer) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .shimmer()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) { }
            } else {
                AnimatedContent(
                    targetState = imageState,
                ) { targetState ->
                    when (targetState) {
                        is AsyncImagePainter.State.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .shimmer()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )
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
            }
        }

        if (!isLoadingShimmer) {
            MediaTitle(
                title = title,
                textStyle = textStyle,
            )
        }
    }
}

@Composable
fun TrackOverlay(
    trackStatusIcon: ImageVector?,
    trackStatusRating: Double?,
) {
    Column(
        modifier = Modifier.padding(4.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End,
    ) {
        /*trackStatusRating?.let {
            Box(
                modifier = Modifier.size(36.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier.size(36.dp),
                    imageVector = Icons.Default.Star,
                    contentDescription = "Status Icon",
                    tint = Color.White,
                )
                Text(
                    text = "${trackStatusRating.toInt()}",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Black,
                )
            }
        }*/

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
    items: List<Any>,
    onClickItem: (MediaNavigationItems) -> Unit = { },
) {
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
                                    MediaNavigationItems(
                                        id = item.id,
                                        title = item.title,
                                        coverUrl = item.coverUrl,
                                        mediaType = item.mediaType
                                    )
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

@Composable
fun PersonImage(
    userImageUrl: String?,
    onClick: (() -> Unit)? = null,
    isUploading: Boolean = false,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier,
) {
    val painter = rememberAsyncImagePainter(userImageUrl)
    val interactionSource = remember { MutableInteractionSource() }

    val paddingFraction = 0.2f
    val iconPadding = size * paddingFraction

    Surface(
        modifier = modifier
            .size(size)
            .clickable(
                enabled = onClick != null,
                onClick = { onClick?.invoke() },
                interactionSource = interactionSource,
                indication = null, // or ripple if you want
            ),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        val state = painter.state.collectAsState().value

        when {
            isUploading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            state is AsyncImagePainter.State.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            state is AsyncImagePainter.State.Success -> {
                Image(
                    painter = painter,
                    contentScale = ContentScale.Crop,
                    contentDescription = "Account Image",
                    modifier = Modifier.fillMaxSize()
                )
            }

            state is AsyncImagePainter.State.Error || state is AsyncImagePainter.State.Empty -> {
                Icon(
                    modifier = Modifier
                        .padding(iconPadding)
                        .fillMaxSize(),
                    imageVector = Icons.Default.Person,
                    contentDescription = "No Image",
                )
            }
        }
    }
}

@Composable
fun ImagePicker(
    imageUrl: String?,
    imageUploadState: ImageUploadState,
    onImagePicked: (ByteArray?) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val launcher = rememberFilePickerLauncher(
        type = PickerType.Image,
        mode = PickerMode.Single,
    ) { file ->
        coroutineScope.launch {
            val fileBytes = file?.readBytes()
            onImagePicked(fileBytes)
        }
    }

    Box(
        modifier = Modifier
            .size(124.dp),
    ) {
        PersonImage(
            modifier = Modifier.size(100.dp).align(Alignment.Center),
            userImageUrl = imageUrl,
            isUploading = imageUploadState == ImageUploadState.Uploading,
            onClick = { launcher.launch() },
        )

        Surface(
            modifier = Modifier.size(42.dp).align(Alignment.BottomEnd),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            onClick = { launcher.launch() }
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
                modifier = Modifier
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun LargerImageDialog(
    showDialog: Boolean,
    imageUrl: String?,
    onDismiss: () -> Unit,
) {
    val painter = rememberAsyncImagePainter(imageUrl)

    if (imageUrl.isNullOrEmpty()) return

    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f),
                shape = MaterialTheme.shapes.medium,
            ) {
                Image(
                    painter = painter,
                    contentDescription = "Account Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
