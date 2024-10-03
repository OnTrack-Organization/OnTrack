package de.ashman.ontrack.media.movie.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieScreen(
    id: Int,
    onBack: () -> Unit,
    viewModel: MovieViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(id) {
        viewModel.fetchMovieDetails(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        content = { Icon(Icons.AutoMirrored.Filled.ArrowBack, "") }
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            uiState.selectedMovie?.let { if (it.title != null) Text(it.title) }
        }
    }
}