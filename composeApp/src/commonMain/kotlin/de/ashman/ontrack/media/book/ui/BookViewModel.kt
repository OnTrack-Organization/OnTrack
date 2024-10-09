package de.ashman.ontrack.media.book.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.book.api.BookRepository
import de.ashman.ontrack.media.book.api.toEntity
import de.ashman.ontrack.media.book.model.domain.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookViewModel(
    private val repository: BookRepository,
    private val userService: UserService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BookUiState())
    val uiState: StateFlow<BookUiState> = _uiState.asStateFlow()

    init {
        fetchBooksByKeyword("harry potter")
    }

    fun fetchBooksByKeyword(keyword: String) {
        viewModelScope.launch {
            val books = repository.fetchMediaByQuery(keyword)
            _uiState.value = _uiState.value.copy(books = books)
        }
    }

    fun fetchBookDetails(book: Book) {
        viewModelScope.launch {
            val bookDetails = repository.fetchMediaDetails(book.id)

            _uiState.value = uiState.value.copy(
                selectedBook = book.copy(description = bookDetails.description)
            )
        }
    }

    fun addToList(book: Book) {
        viewModelScope.launch {
            userService.updateUserMedia(book.toEntity())
        }
    }
}
