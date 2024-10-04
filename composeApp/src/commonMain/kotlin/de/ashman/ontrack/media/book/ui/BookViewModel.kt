package de.ashman.ontrack.media.book.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.media.book.api.BookRepository
import de.ashman.ontrack.media.book.model.domain.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookViewModel(
    private val repository: BookRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BookUiState())
    val uiState: StateFlow<BookUiState> = _uiState.asStateFlow()

    init {
        fetchBooksByKeyword("harry potter")
    }

    fun fetchBooksByKeyword(keyword: String) {
        viewModelScope.launch {
            val books = repository.fetchMediaByKeyword(keyword)
            _uiState.value = _uiState.value.copy(books = books)
        }
    }

    fun fetchBookDetails(book: Book) {
        // TODO
    }
}
