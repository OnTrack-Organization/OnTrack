package de.ashman.ontrack.shelf.ui

import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.MediaViewModel
import de.ashman.ontrack.api.book.BookRepository
import de.ashman.ontrack.di.DEFAULT_BOOK_QUERY
import de.ashman.ontrack.media.domain.Book
import de.ashman.ontrack.media.domain.MediaType

class BookViewModel(
    private val repository: BookRepository,
    private val userService: UserService,
) : MediaViewModel<Book>(repository, userService) {

    init {
        fetchMediaByQuery(DEFAULT_BOOK_QUERY)
        fetchStatusCounts(MediaType.BOOK)
    }
}
