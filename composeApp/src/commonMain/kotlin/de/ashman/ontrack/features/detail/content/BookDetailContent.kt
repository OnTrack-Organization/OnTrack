package de.ashman.ontrack.features.detail.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HideSource
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import de.ashman.ontrack.domain.Author
import de.ashman.ontrack.domain.Book
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.features.common.MediaChips
import de.ashman.ontrack.features.common.MediaDescription
import de.ashman.ontrack.features.common.MediaPosterRow
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_author
import ontrack.composeapp.generated.resources.detail_author_books
import ontrack.composeapp.generated.resources.detail_description
import ontrack.composeapp.generated.resources.detail_genres
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.BookDetailContent(
    book: Book,
    onClickItem: (Media) -> Unit = { },
) {
    item {
        AuthorCard(book.author)
    }
    item {
        MediaDescription(
            title = stringResource(Res.string.detail_description),
            description = book.description,
        )
    }
    item {
        MediaChips(
            title = stringResource(Res.string.detail_genres),
            items = book.genres,
        )
    }
    item {
        MediaPosterRow(
            title = stringResource(Res.string.detail_author_books, book.author.booksCount ?: 0),
            items = book.author.books,
            onClickItem = onClickItem,
        )
    }
}

@Composable
fun AuthorCard(author: Author) {
    var expanded by remember { mutableStateOf(false) }

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
                text = stringResource(Res.string.detail_author),
                style = MaterialTheme.typography.titleMedium,
            )
            author.bio?.let {
                Icon(imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, "Arrow")
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SubcomposeAsyncImage(
                model = author.imageUrl,
                contentScale = ContentScale.Crop,
                contentDescription = "Author Image",
                modifier = Modifier
                    .size(42.dp)
                    .clip(shape = CircleShape)
            ) {
                val state = painter.state.collectAsState().value

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    when (state) {
                        is AsyncImagePainter.State.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                        }

                        is AsyncImagePainter.State.Error -> {
                            Icon(
                                modifier = Modifier.padding(8.dp),
                                imageVector = Icons.Default.HideSource,
                                contentDescription = "No Image",
                            )
                        }

                        else -> {
                            SubcomposeAsyncImageContent()
                        }
                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.Center,
            ) {
                author.name?.let {
                    Text(
                        text = author.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
                author.birthDate?.let {
                    Text(
                        text = author.birthDate,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        author.bio?.let {
            if (expanded) {
                Text(
                    text = author.bio,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}