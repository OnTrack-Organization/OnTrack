package de.ashman.ontrack.features.init.start

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.api.ApiType
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.features.common.DEFAULT_POSTER_HEIGHT
import de.ashman.ontrack.features.common.MediaPoster
import de.ashman.ontrack.features.common.OnTrackButton
import de.ashman.ontrack.features.common.OnTrackOutlinedButton
import de.ashman.ontrack.util.getMediaTypeUi
import kotlinx.coroutines.delay
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.api_powered_by
import ontrack.composeapp.generated.resources.app_name
import ontrack.composeapp.generated.resources.app_subtitle
import ontrack.composeapp.generated.resources.intro_button
import ontrack.composeapp.generated.resources.login_button
import ontrack.composeapp.generated.resources.shelves_filled
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

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
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(color = MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.shelves_filled),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.fillMaxSize().padding(8.dp),
                            )
                        }

                        Text(
                            text = stringResource(Res.string.app_name),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Text(
                        text = stringResource(Res.string.app_subtitle),
                        style = MaterialTheme.typography.headlineSmall,
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
    val itemTypes = MediaType.entries.map { it.getMediaTypeUi() }
    val realPageCount = itemCovers.size
    val infiniteMultiplier = 1000
    val pageCount = realPageCount * infiniteMultiplier
    val initialPage = pageCount / 2

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        initialPageOffsetFraction = 0f,
        pageCount = { pageCount }
    )

    HorizontalPager(
        state = pagerState,
        userScrollEnabled = false,
        modifier = modifier
    ) { index ->
        val realIndex = index % realPageCount
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
                    Box(
                        modifier = Modifier.size(42.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            modifier = Modifier.fillMaxSize().padding(4.dp),
                            imageVector = itemTypes[realIndex].outlinedIcon,
                            contentDescription = "Media Icon",
                        )
                    }
                    Text(
                        text = pluralStringResource(itemTypes[realIndex].title, 2),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
                MediaPoster(
                    modifier = Modifier.height(DEFAULT_POSTER_HEIGHT),
                    coverUrl = itemCovers[realIndex],
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            val nextPage = pagerState.currentPage + 1
            pagerState.animateScrollToPage(
                page = nextPage,
                animationSpec = tween(
                    durationMillis = 400,
                    easing = FastOutLinearInEasing
                )
            )
        }
    }
}

@Composable
fun ApiContributions() {
    val uriHandler = LocalUriHandler.current

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
                        .clip(MaterialTheme.shapes.small)
                        .clickable {
                            item.uri?.let {
                                uriHandler.openUri(it)
                            }
                        },
                )
            }
        }
    }
}