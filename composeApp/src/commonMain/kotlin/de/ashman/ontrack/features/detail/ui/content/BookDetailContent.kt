package de.ashman.ontrack.features.detail.ui.content

import androidx.compose.runtime.Composable
import de.ashman.ontrack.features.detail.ui.MediaDescription
import de.ashman.ontrack.features.detail.ui.MediaGenres
import de.ashman.ontrack.domain.Book

@Composable
fun BookDetailContent(
    book: Book,
    onClickItem: (String) -> Unit = { },
) {
    MediaDescription(
        description = book.description,
    )

    MediaGenres(
        genres = book.subjects,
    )
}