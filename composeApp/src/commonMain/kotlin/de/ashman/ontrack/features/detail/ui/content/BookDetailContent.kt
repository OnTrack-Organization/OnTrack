package de.ashman.ontrack.features.detail.ui.content

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import de.ashman.ontrack.features.detail.ui.MediaDescription
import de.ashman.ontrack.features.detail.ui.MediaGenres
import de.ashman.ontrack.domain.Book

fun LazyListScope.BookDetailContent(
    book: Book,
    onClickItem: (String) -> Unit = { },
) {
    item {
        MediaDescription(book.description)
    }
    item {
        MediaGenres(book.subjects)
    }
}