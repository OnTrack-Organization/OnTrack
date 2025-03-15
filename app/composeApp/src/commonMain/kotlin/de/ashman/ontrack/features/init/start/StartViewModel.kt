package de.ashman.ontrack.features.init.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class StartViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow(StartState())
    val uiState = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _uiState.value,
        )
}

data class StartState(
    val itemCovers: List<String> = listOf(
        "https://image.tmdb.org/t/p/original/39wmItIWsg5sZMyRUHLkWBcuVCM.jpg",
        "https://image.tmdb.org/t/p/original/c86aUAhACPyloPtL2CH4ZP5hO7V.jpg",
        "https://covers.openlibrary.org/b/id/11200092-L.jpg",
        "https:////images.igdb.com/igdb/image/upload/t_1080p/co2255.jpg",
        "https://cf.geekdo-images.com/F_KDEu0GjdClml8N7c8Imw__original/img/gcX_EfjsRpB5fI4Ug4XV73G4jGI=/0x0/filters:format(jpeg)/pic2582929.jpg",
        "https://i.scdn.co/image/ab67616d0000b273b361ce46dbadbf8a11081b60",
    ),
)