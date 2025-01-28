package de.ashman.ontrack.features.detail.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import de.ashman.ontrack.domain.Boardgame
import de.ashman.ontrack.domain.BoardgameDesigner
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.features.common.MediaDescription
import de.ashman.ontrack.features.common.MediaPosterRow
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_description
import ontrack.composeapp.generated.resources.detail_designer
import ontrack.composeapp.generated.resources.detail_franchise
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.BoardgameDetailContent(
    boardgame: Boardgame,
    onClickItem: (Media) -> Unit = { },
) {
    item {
        BoardgameDesignerCard(designer = boardgame.designer)
    }
    item {
        MediaDescription(
            title = stringResource(Res.string.detail_description),
            description = boardgame.description,
        )
    }
    item {
        MediaPosterRow(
            title = stringResource(Res.string.detail_franchise),
            items = boardgame.franchise,
            onClickItem = onClickItem,
        )
    }
}

@Composable
fun BoardgameDesignerCard(designer: BoardgameDesigner?) {
    val painter = rememberAsyncImagePainter(designer?.imageUrl)

    designer?.let {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(Res.string.detail_designer),
                style = MaterialTheme.typography.titleMedium,
            )

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
                                contentDescription = "Designer Image",
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
                        text = designer.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
