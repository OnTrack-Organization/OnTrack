package de.ashman.ontrack.book.ui

import de.ashman.ontrack.book.model.Book

data class BookUiState(
    val books: List<Book> = emptyList(),
)