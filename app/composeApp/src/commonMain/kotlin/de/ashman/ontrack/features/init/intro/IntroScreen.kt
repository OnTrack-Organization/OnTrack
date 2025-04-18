package de.ashman.ontrack.features.init.intro

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.features.common.OnTrackButton
import de.ashman.ontrack.features.common.OnTrackTopBar
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.intro_title
import ontrack.composeapp.generated.resources.login_button
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntroScreen(
    onGoToLogin: () -> Unit,
    onBack: () -> Unit,
) {
    val pages = listOf<IntroPage>(
        IntroPage.FIRST_PAGE,
        IntroPage.SECOND_PAGE,
        IntroPage.THIRD_PAGE,
        IntroPage.FOURTH_PAGE,
    )
    val pagerState = rememberPagerState(initialPage = 0) { pages.size }

    Scaffold(
        topBar = {
            OnTrackTopBar(
                title = stringResource(Res.string.intro_title),
                navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
                onClickNavigation = onBack
            )
        },
        bottomBar = {
            PagerIndicator(
                pageSize = pages.size,
                currentPage = pagerState.currentPage,
            )
        }
    ) { contentPadding ->
        val showLogin = pagerState.settledPage == pages.lastIndex && pagerState.currentPage == pages.lastIndex

        HorizontalPager(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 16.dp,
            pageSize = PageSize.Fill,
            beyondViewportPageCount = 0,
        ) { index ->
            IntroContent(
                introPage = pages[index],
                showLogin = showLogin,
                onGoToLogin = onGoToLogin
            )
        }
    }
}

@Composable
fun IntroContent(
    introPage: IntroPage,
    showLogin: Boolean,
    onGoToLogin: () -> Unit,
) {
    val isDark = isSystemInDarkTheme()

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            Box(
                modifier = Modifier
                    .width(170.dp)
                    .shadow(8.dp, shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(if (isDark) introPage.imageDark else introPage.imageLight),
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = stringResource(introPage.title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = stringResource(introPage.description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }

        AnimatedVisibility(visible = showLogin) {
            OnTrackButton(
                modifier = Modifier.fillMaxWidth(),
                text = Res.string.login_button,
                onClick = onGoToLogin,
            )
        }
    }
}

@Composable
fun PagerIndicator(
    pageSize: Int,
    currentPage: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageSize) {
            val color = if (it == currentPage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(16.dp)
            )
        }
    }
}