package de.ashman.ontrack.features.detail.content

import androidx.compose.foundation.lazy.LazyListScope
import de.ashman.ontrack.api.utils.getLivingDates
import de.ashman.ontrack.domain.media.Book
import de.ashman.ontrack.features.common.MediaPosterRow
import de.ashman.ontrack.features.detail.components.CreatorCard
import de.ashman.ontrack.features.detail.components.MediaChips
import de.ashman.ontrack.features.detail.components.MediaDescription
import de.ashman.ontrack.navigation.MediaNavigationItems
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_author
import ontrack.composeapp.generated.resources.detail_author_books
import ontrack.composeapp.generated.resources.detail_description
import ontrack.composeapp.generated.resources.detail_genres
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.BookDetailContent(
    book: Book,
    onClickItem: (MediaNavigationItems) -> Unit,
) {
    item {
        MediaDescription(
            title = stringResource(Res.string.detail_description),
            description = book.description,
        )
    }

    item {
        CreatorCard(
            title = Res.string.detail_author,
            name = book.author?.name,
            subInfo = getLivingDates(book.author?.birthDate, book.author?.deathDate),
            description = book.author?.bio,
            imageUrl = book.author?.imageUrl,
        )
    }

    book.genres?.let {
        item {
            MediaChips(
                title = stringResource(Res.string.detail_genres),
                items = it,
            )
        }
    }

    book.author?.books?.let {
        item {
            MediaPosterRow(
                title = book.author.booksCount?.let {
                    stringResource(Res.string.detail_author_books, it)
                } ?: stringResource(Res.string.detail_author_books),
                items = it,
                onClickItem = onClickItem,
            )
        }
    }
}
