package de.ashman.ontrack.book.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.book.api.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookViewModel(
    private val bookRepository: BookRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BookUiState())
    val uiState: StateFlow<BookUiState> = _uiState.asStateFlow()

    init {
        fetchPopular()
    }

    fun fetchPopular() {
        viewModelScope.launch {
            bookRepository.fetchPopular().collect { books ->
                if (books != null) _uiState.value = _uiState.value.copy(books = books)
                books?.forEach {
                    println(it.title)
                }
            }
        }
    }
}
