package de.ashman.ontrack.shelf.ui

import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.MediaViewModel
import de.ashman.ontrack.api.book.BookRepository
import de.ashman.ontrack.media.domain.Book
import de.ashman.ontrack.media.domain.MediaType

class BookViewModel(
    private val repository: BookRepository,
    private val userService: UserService,
) : MediaViewModel<Book>(repository, userService) {

    init {
        fetchMediaByQuery("harry potter")
        fetchStatusCounts(MediaType.BOOK)
    }
}
