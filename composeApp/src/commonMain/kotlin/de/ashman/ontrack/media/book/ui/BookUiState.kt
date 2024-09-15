package de.ashman.ontrack.media.book.ui

import de.ashman.ontrack.media.book.model.Book

data class BookUiState(
    val books: List<Book> = emptyList(),
)