package de.ashman.ontrack.features.init.start

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.api.ApiType
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.features.common.DEFAULT_POSTER_HEIGHT
import de.ashman.ontrack.features.common.MediaPoster
import de.ashman.ontrack.features.common.OnTrackButton
import de.ashman.ontrack.features.common.OnTrackOutlinedButton
import de.ashman.ontrack.util.getMediaTypeUi
import kotlinx.coroutines.delay
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.api_powered_by
import ontrack.composeapp.generated.resources.app_name
import ontrack.composeapp.generated.resources.intro_button
import ontrack.composeapp.generated.resources.login_button
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun StartScreen(
    viewModel: StartViewModel,
    onGoToIntro: () -> Unit,
    onGoToLogin: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding).padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            AutoScrollPoster(
                itemCovers = uiState.itemCovers,
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = stringResource(Res.string.app_name),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OnTrackButton(
                        text = Res.string.intro_button,
                        onClick = onGoToIntro,
                    )
                    OnTrackOutlinedButton(
                        text = Res.string.login_button,
                        onClick = onGoToLogin,
                    )
                }
            }

            ApiContributions()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutoScrollPoster(
    modifier: Modifier = Modifier,
    itemCovers: List<String>,
) {
    // TODO fix endless scroll animation
    val itemTypes = MediaType.entries.map { it.getMediaTypeUi() }
    val pagerState = rememberPagerState(initialPage = 0) { itemCovers.size }

    HorizontalPager(
        state = pagerState,
        userScrollEnabled = false,
    ) { index ->
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = itemTypes[index].icon,
                        contentDescription = "Media Icon",
                    )
                    Text(
                        text = pluralStringResource(itemTypes[index].title, 2),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
                MediaPoster(
                    modifier = Modifier.height(DEFAULT_POSTER_HEIGHT),
                    coverUrl = itemCovers[index],
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(
                page = nextPage,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = FastOutLinearInEasing
                )
            )
        }
    }
}

@Composable
fun ApiContributions() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.api_powered_by),
            style = MaterialTheme.typography.titleMedium,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ApiType.entries.forEachIndexed { index, item ->
                if (item == ApiType.OnTrack) return@forEachIndexed

                Image(
                    painter = painterResource(item.icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(MaterialTheme.shapes.small),
                )
            }
        }
    }
}