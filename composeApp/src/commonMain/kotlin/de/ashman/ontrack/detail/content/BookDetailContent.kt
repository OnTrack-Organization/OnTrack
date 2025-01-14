package de.ashman.ontrack.detail.content

import androidx.compose.runtime.Composable
import de.ashman.ontrack.detail.MediaDescription
import de.ashman.ontrack.detail.MediaGenres
import de.ashman.ontrack.media.model.Book

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