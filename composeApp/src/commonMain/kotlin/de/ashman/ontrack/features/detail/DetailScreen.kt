package de.ashman.ontrack.features.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ashman.ontrack.domain.tracking.Tracking
import de.ashman.ontrack.features.detail.components.ErrorContent
import de.ashman.ontrack.features.detail.components.LoadingContent
import de.ashman.ontrack.features.detail.components.StickyMainContent
import de.ashman.ontrack.features.tracking.CurrentBottomSheetContent
import de.ashman.ontrack.features.tracking.TrackingBottomSheetContent
import de.ashman.ontrack.util.getMediaTypeUi
import kotlinx.coroutines.launch
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.tracking_deleted
import ontrack.composeapp.generated.resources.tracking_saved
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.pluralStringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    tracking: Tracking,
    viewModel: DetailViewModel,
    onClickItem: (String) -> Unit,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var currentBottomSheet by remember { mutableStateOf(CurrentBottomSheetContent.TRACKING) }
    val coroutineScope = rememberCoroutineScope()

    val listState = rememberLazyListState()

    LaunchedEffect(tracking.mediaId) {
        viewModel.fetchDetails(tracking)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = tracking.mediaType.getMediaTypeUi().icon,
                            contentDescription = "Media Type Icon"
                        )
                        Text(
                            text = pluralStringResource(tracking.mediaType.getMediaTypeUi().title, 1),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back Icon")
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
        ) {
            StickyMainContent(
                imageModifier = Modifier.padding(horizontal = 16.dp),
                media = uiState.selectedMedia,
                mediaType = tracking.mediaType,
                mediaTitle = tracking.mediaTitle,
                mediaCoverUrl = tracking.mediaCoverUrl,
                status = uiState.selectedTracking?.status,
                scrollBehavior = scrollBehavior,
                onClickAddTracking = {
                    currentBottomSheet = CurrentBottomSheetContent.TRACKING
                    showBottomSheet = true
                },
                onClickRemoveTracking = {
                    currentBottomSheet = CurrentBottomSheetContent.DELETE
                    showBottomSheet = true
                },
            )


            when (uiState.resultState) {
                DetailResultState.Loading -> LoadingContent()

                DetailResultState.Error -> ErrorContent(text = tracking.mediaType.getMediaTypeUi().error)

                DetailResultState.Success -> {
                    uiState.selectedMedia?.let {
                        DetailContent(
                            media = it,
                            tracking = uiState.selectedTracking,
                            listState = listState,
                            onClickItem = onClickItem,
                        )
                    }
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
            ) {
                TrackingBottomSheetContent(
                    currentContent = currentBottomSheet,
                    mediaId = tracking.mediaId,
                    mediaType = tracking.mediaType,
                    mediaTitle = tracking.mediaTitle,
                    mediaCoverUrl = tracking.mediaCoverUrl,
                    tracking = tracking,
                    onSaveTracking = {
                        viewModel.saveTracking(it)
                        showBottomSheet = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(getString(Res.string.tracking_saved))
                        }
                    },
                    onDeleteTrackings = {
                        viewModel.deleteTrackings()
                        showBottomSheet = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(getString(Res.string.tracking_deleted))
                        }
                    },
                    onCancel = {
                        showBottomSheet = false
                    },
                    goToReview = {
                        currentBottomSheet = CurrentBottomSheetContent.REVIEW
                    }
                )
            }
        }
    }
}
