package de.ashman.ontrack.features.detail.content

import androidx.compose.runtime.Composable
import de.ashman.ontrack.features.detail.MediaDescription
import de.ashman.ontrack.features.detail.MediaGenres
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