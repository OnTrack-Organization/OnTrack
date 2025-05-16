package de.ashman.ontrack.features.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.valentinilk.shimmer.shimmer
import de.ashman.ontrack.domain.media.Franchise
import de.ashman.ontrack.domain.media.Media
import de.ashman.ontrack.domain.media.Season
import de.ashman.ontrack.features.detail.components.MediaTitle
import de.ashman.ontrack.features.settings.ImageUploadState
import de.ashman.ontrack.navigation.MediaNavigationParam
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_images
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs
import kotlin.math.roundToInt

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
    onClickItem: (MediaNavigationParam) -> Unit = { },
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
                                    MediaNavigationParam(
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
    profilePictureUrl: String?,
    onClick: (() -> Unit)? = null,
    isUploading: Boolean = false,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier,
) {
    val painter = rememberAsyncImagePainter(profilePictureUrl)
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
                indication = null,
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
    profilePictureUrl: String?,
    imageUploadState: ImageUploadState,
    onProfilePictureSelected: (ByteArray?) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val launcher = rememberFilePickerLauncher(
        type = PickerType.Image,
        mode = PickerMode.Single,
    ) { file ->
        coroutineScope.launch {
            val fileBytes = file?.readBytes()
            onProfilePictureSelected(fileBytes)
        }
    }

    Box(
        modifier = Modifier
            .size(124.dp),
    ) {
        PersonImage(
            modifier = Modifier.size(100.dp).align(Alignment.Center),
            profilePictureUrl = profilePictureUrl,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LargerImageDialog(
    showDialog: Boolean,
    imageUrl: String?,
    onDismiss: () -> Unit,
) {
    if (imageUrl.isNullOrEmpty()) return

    if (showDialog) {
        BasicAlertDialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = onDismiss
        ) {
            val swipeOffset = remember { mutableStateOf(0f) }
            val animatedOffset by animateFloatAsState(
                targetValue = swipeOffset.value,
                label = "Swipe Animation"
            )

            val dismissThreshold = 400f

            val zoomState = rememberZoomState()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium)
                    .zoomable(zoomState)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragEnd = {
                                if (abs(swipeOffset.value) > dismissThreshold) {
                                    onDismiss()
                                } else {
                                    swipeOffset.value = 0f
                                }
                            }
                        ) { _, dragAmount ->
                            if (zoomState.scale == 1f) {
                                swipeOffset.value += dragAmount
                            }
                        }
                    }
            ) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Account Image",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxSize()
                        .offset { IntOffset(0, animatedOffset.roundToInt()) }
                )
            }
        }
    }
}

@Composable
fun MediaImageRow(
    imageUrls: List<String>,
    onClickImage: (String) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(Res.string.detail_images),
            style = MaterialTheme.typography.titleMedium,
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            items(imageUrls) { imageUrl ->
                val painter = rememberAsyncImagePainter(imageUrl)

                Surface(
                    modifier = Modifier
                        .size(124.dp),
                    shape = MaterialTheme.shapes.medium,
                    onClick = { onClickImage(imageUrl) },
                ) {
                    Image(
                        painter = painter,
                        contentDescription = "Account Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}