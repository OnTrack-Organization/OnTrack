package de.ashman.ontrack.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.ashman.ontrack.ApiTest
import de.ashman.ontrack.OnTrackScreen

@Composable
fun HomeScreen(
    onClickNavItem: (Any) -> Unit = {},
    goToDetail: (String) -> Unit,
) {
    OnTrackScreen(
        onClickNavItem = onClickNavItem,
        // TODO shared resources
        topBarTitle = { },
        topBarNavIcon = {
            /*Icon(
                painter = painterResource(R.drawable.img_takedown),
                contentDescription = "Takedown Icon",
                modifier =
                    Modifier
                        .size(64.dp)
                        .padding(4.dp),
                tint = Color.Unspecified,
            )*/
        },
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
    goToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ApiTest()
}
