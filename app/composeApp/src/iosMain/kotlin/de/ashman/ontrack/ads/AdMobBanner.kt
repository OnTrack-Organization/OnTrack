package de.ashman.ontrack.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import de.ashman.ontrack.generateIOSBanner
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun AdMobBanner(
    modifier: Modifier
) {
    UIKitView(
        factory = { generateIOSBanner().view },
        modifier = modifier,
    )
}