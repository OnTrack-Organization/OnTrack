package de.ashman.ontrack.features.init.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.login_button
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun IntroScreen(
    onGoToLogin: () -> Unit,
) {
    val pages = listOf<IntroPage>(
        IntroPage.FIRST_PAGE,
        IntroPage.SECOND_PAGE,
        IntroPage.THIRD_PAGE,
    )

    val pagerState = rememberPagerState(initialPage = 0) { pages.size }

    Scaffold(
        bottomBar = {
            PagerIndicator(
                pageSize = pages.size,
                currentPage = pagerState.currentPage,
                onGoToLogin = onGoToLogin,
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier.padding(contentPadding)
        ) {
            HorizontalPager(state = pagerState) { index ->
                IntroContent(introPage = pages[index])
            }
        }
    }
}

@Composable
fun PagerIndicator(
    pageSize: Int,
    currentPage: Int,
    onGoToLogin: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(pageSize) {
            val color = if (it == currentPage) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.secondaryContainer

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(16.dp)
            )
        }

        if (currentPage == pageSize - 1) {
            Button(
                onClick = onGoToLogin,
            ) {
                Text(stringResource(Res.string.login_button))
            }
        }
    }
}

@Composable
fun IntroContent(introPage: IntroPage) {
    Column {
        Image(
            painter = painterResource(introPage.image),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
        )

        Text(
            text = stringResource(introPage.title),
            style = MaterialTheme.typography.titleLarge,
        )

        Text(
            text = stringResource(introPage.description),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}