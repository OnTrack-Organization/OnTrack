package de.ashman.ontrack.preview

import androidx.compose.ui.tooling.preview.Preview
import de.ashman.ontrack.domain.media.Show
import de.ashman.ontrack.domain.user.User

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
annotation class OnTrackPreview

val previewUser = User(
    id = "1",
    username = "za_warudo_brando",
    fcmToken = "test_token",
    name = "Dio Brando",
    imageUrl = "",
    email = "dio.brando@theworld.com"
)

val previewMedia = Show(
    id = "1",
    title = "Jojo's Bizarre Adventure",
    coverUrl = null,
    detailUrl = "",
)

val previewTimestampNow = System.currentTimeMillis()
val previewTimestampHourAgo = System.currentTimeMillis() - 60 * 60 * 1000
val previewTimestampYesterday = System.currentTimeMillis() - 24 * 60 * 60 * 1000
val previewTimestampWeekAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000