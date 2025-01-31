package de.ashman.ontrack.features.detail.content

import androidx.compose.foundation.lazy.LazyListScope
import de.ashman.ontrack.api.utils.getLivingDates
import de.ashman.ontrack.domain.Book
import de.ashman.ontrack.domain.Media
import de.ashman.ontrack.features.common.CreatorCard
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

    item {
        MediaChips(
            title = stringResource(Res.string.detail_genres),
            items = book.genres,
        )
    }

    item {
        MediaPosterRow(
            title = book.author?.booksCount?.let {
                stringResource(Res.string.detail_author_books, it)
            } ?: stringResource(Res.string.detail_author_books),
            items = book.author?.books,
            onClickItem = onClickItem,
        )
    }
}
