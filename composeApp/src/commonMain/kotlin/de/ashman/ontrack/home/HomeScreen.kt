package de.ashman.ontrack.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.ashman.ontrack.ApiTest
import de.ashman.ontrack.OnTrackScreen

@Composable
fun HomeScreen(
    onClickNavItem: (Any) -> Unit = {},
    goToDetail: (Int) -> Unit,
) {
    OnTrackScreen(
        onClickNavItem = onClickNavItem,
    ) { innerPadding ->
        HomeContent(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
            goToDetail = goToDetail,
        )
    }
}

@Composable
fun HomeContent(
    goToDetail: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    //ApiTest(modifier = modifier, goToDetail)
}
