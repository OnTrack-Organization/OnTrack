package de.ashman.ontrack.features.detail.ui.content

import androidx.compose.foundation.lazy.LazyListScope
import de.ashman.ontrack.domain.Book
import de.ashman.ontrack.domain.Media
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_genres
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.BookDetailContent(
    book: Book,
    onClickItem: (Media) -> Unit = { },
) {
    item {
        MediaDescription(book.description)
    }
    item {
        MediaChips(
            title = stringResource(Res.string.detail_genres),
            items = book.subjects,
        )
    }
}