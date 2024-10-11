package de.ashman.ontrack.media.book.ui

import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.MediaViewModel
import de.ashman.ontrack.media.book.api.BookRepository
import de.ashman.ontrack.media.model.Book
import de.ashman.ontrack.media.model.MediaType

class BookViewModel(
    private val repository: BookRepository,
    private val userService: UserService,
) : MediaViewModel<Book>(repository, userService) {

    init {
        fetchMediaByQuery("harry potter")
        fetchStatusCounts(MediaType.BOOK)
    }
}
