package de.ashman.ontrack.features.detail.media

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import de.ashman.ontrack.domain.media.Boardgame
import de.ashman.ontrack.domain.media.BoardgameImage
import de.ashman.ontrack.features.common.MediaPosterRow
import de.ashman.ontrack.features.detail.components.CreatorCard
import de.ashman.ontrack.features.detail.components.MediaDescription
import de.ashman.ontrack.navigation.MediaNavigationItems
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_description
import ontrack.composeapp.generated.resources.detail_designer
import ontrack.composeapp.generated.resources.detail_franchise
import ontrack.composeapp.generated.resources.detail_images
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.BoardgameDetailContent(
    boardgame: Boardgame,
    onClickItem: (MediaNavigationItems) -> Unit,
    onClickImage: (String) -> Unit,
) {
    item {
        MediaDescription(
            title = stringResource(Res.string.detail_description),
            description = boardgame.description,
        )
    }

    boardgame.designer?.let {
        item {
            CreatorCard(
                title = Res.string.detail_designer,
                name = it.name,
                imageUrl = boardgame.designer.imageUrl,
                description = boardgame.designer.bio,
                subInfo = null,
            )
        }
    }

    boardgame.franchise?.let {
        item {
            MediaPosterRow(
                title = stringResource(Res.string.detail_franchise),
                items = it,
                onClickItem = onClickItem,
            )
        }
    }

    boardgame.images?.let {
        item {
            MediaImageRow(
                title = stringResource(Res.string.detail_images),
                items = it,
                onClickImage = onClickImage,
            )
        }
    }
}

@Composable
fun MediaImageRow(
    title: String,
    items: List<BoardgameImage>,
    onClickImage: (String) -> Unit,
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
                val painter = rememberAsyncImagePainter(item.imageUrl)

                Surface(
                    modifier = Modifier
                        .size(124.dp),
                    shape = MaterialTheme.shapes.medium,
                    onClick = { onClickImage(item.imageUrl) },
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