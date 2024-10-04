package de.ashman.ontrack.media.book.ui

import de.ashman.ontrack.media.book.model.domain.Book

data class BookUiState(
    val books: List<Book> = emptyList(),
    val selectedBook: Book? = null,
)