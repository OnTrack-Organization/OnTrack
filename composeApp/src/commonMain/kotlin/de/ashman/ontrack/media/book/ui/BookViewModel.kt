package de.ashman.ontrack.media.book.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.login.UserService
import de.ashman.ontrack.media.MediaUiState
import de.ashman.ontrack.media.book.api.BookRepository
import de.ashman.ontrack.media.book.model.domain.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookViewModel(
    private val repository: BookRepository,
    private val userService: UserService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MediaUiState<Book>())
    val uiState: StateFlow<MediaUiState<Book>> = _uiState.asStateFlow()

    init {
        fetchBooksByQuery("harry potter")
    }

    fun fetchBooksByQuery(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.fetchMediaByQuery(query)

            _uiState.value = result.fold(
                onSuccess = { books ->
                    _uiState.value.copy(
                        mediaList = books,
                        isLoading = false,
                    )
                },
                onFailure = { throwable ->
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message
                    )
                }
            )
        }
    }

    // TODO change so that id itself is sufficient
    fun fetchBookDetails(book: Book) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.fetchMediaDetails(book.id)

            _uiState.value = result.fold(
                onSuccess = { book ->
                    _uiState.value.copy(
                        selectedMedia = book.copy(description = book.description),
                        isLoading = false,
                        errorMessage = null
                    )
                },
                onFailure = { throwable ->
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message
                    )
                }
            )
        }
    }

    fun addToList(book: Book) {
        viewModelScope.launch {
            userService.updateUserMedia(book)
        }
    }
}
