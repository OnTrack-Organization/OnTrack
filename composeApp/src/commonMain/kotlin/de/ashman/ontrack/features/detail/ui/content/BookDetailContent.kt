package de.ashman.ontrack.features.detail.ui.content

import androidx.compose.foundation.lazy.LazyListScope
import de.ashman.ontrack.domain.Book
import de.ashman.ontrack.domain.Media

fun LazyListScope.BookDetailContent(
    book: Book,
    onClickItem: (Media) -> Unit = { },
) {
    item {
        MediaDescription(book.description)
    }
    item {
        MediaGenres(book.subjects)
    }
}