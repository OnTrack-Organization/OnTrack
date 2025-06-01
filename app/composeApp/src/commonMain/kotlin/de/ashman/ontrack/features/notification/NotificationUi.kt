package de.ashman.ontrack.features.notification

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import de.ashman.ontrack.domain.notification.FriendRequestAccepted
import de.ashman.ontrack.domain.notification.FriendRequestReceived
import de.ashman.ontrack.domain.notification.Mentioned
import de.ashman.ontrack.domain.notification.Notification
import de.ashman.ontrack.domain.notification.PostCommented
import de.ashman.ontrack.domain.notification.PostLiked
import de.ashman.ontrack.domain.notification.RecommendationReceived
import kotlinx.datetime.Clock.System
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.notifications_comment
import ontrack.composeapp.generated.resources.notifications_friend_request_accepted
import ontrack.composeapp.generated.resources.notifications_friend_request_received
import ontrack.composeapp.generated.resources.notifications_like
import ontrack.composeapp.generated.resources.notifications_recommendation
import ontrack.composeapp.generated.resources.time_days_ago
import ontrack.composeapp.generated.resources.time_hours_ago
import ontrack.composeapp.generated.resources.time_just_now
import ontrack.composeapp.generated.resources.time_minutes_ago
import ontrack.composeapp.generated.resources.time_months_ago
import ontrack.composeapp.generated.resources.time_weeks_ago
import ontrack.composeapp.generated.resources.time_years_ago
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.milliseconds

sealed class NotificationUi(
    val icon: ImageVector,
    val message: @Composable (Notification) -> AnnotatedString
) {
    data object RequestReceived : NotificationUi(
        icon = Icons.Default.GroupAdd,
        message = { notification ->
            val n = notification as FriendRequestReceived
            buildAnnotatedString {
                val text = stringResource(
                    Res.string.notifications_friend_request_received,
                    n.sender.username
                )
                formatAnnotatedString(text, n.sender.username)
            }
        }
    )

    data object RequestAccepted : NotificationUi(
        icon = Icons.Default.Group,
        message = { notification ->
            val n = notification as FriendRequestAccepted
            buildAnnotatedString {
                val text = stringResource(
                    Res.string.notifications_friend_request_accepted,
                    n.sender.username
                )
                formatAnnotatedString(text, n.sender.username)
            }
        }
    )

    data object Like : NotificationUi(
        icon = Icons.Default.Favorite,
        message = { notification ->
            val n = notification as PostLiked
            buildAnnotatedString {
                val text = stringResource(
                    Res.string.notifications_like,
                    n.sender.username,
                    n.post.tracking.media.title,
                )
                formatAnnotatedString(text, n.sender.username, n.post.tracking.media.title)
            }
        }
    )

    data object Comment : NotificationUi(
        icon = Icons.AutoMirrored.Default.Chat,
        message = { notification ->
            val n = notification as PostCommented
            buildAnnotatedString {
                val text = stringResource(
                    Res.string.notifications_comment,
                    n.sender.username,
                    n.post.tracking.media.title
                )
                formatAnnotatedString(text, n.sender.username, n.post.tracking.media.title)
            }
        }
    )

    data object Recommendation : NotificationUi(
        icon = Icons.Default.VolunteerActivism,
        message = { notification ->
            val n = notification as RecommendationReceived
            buildAnnotatedString {
                val text = stringResource(
                    Res.string.notifications_recommendation,
                    n.sender.username,
                    n.recommendation.media.title
                )
                formatAnnotatedString(text, n.sender.username, n.recommendation.media.title)
            }
        }
    )

    // TODO later
    /*data object Mention : NotificationTypeUi(
        icon = Icons.Default.AlternateEmail,
        message = { notification ->
            val n = notification as Mentioned
            buildAnnotatedString {
                val text = stringResource(
                    Res.string.notifications_reply,
                    n.sender.username,
                    n.comment.mentionedUserName.orEmpty(),
                    n.post.tracking.media.title
                )
                formatAnnotatedString(text, n.sender.username, n.comment.mentionedUserName, n.post.tracking.media.title)
            }
        }
    )*/
}

fun Notification.getUiType(): NotificationUi {
    return when (this) {
        is FriendRequestReceived -> NotificationUi.RequestReceived
        is FriendRequestAccepted -> NotificationUi.RequestAccepted
        is PostLiked -> NotificationUi.Like
        is PostCommented -> NotificationUi.Comment
        is RecommendationReceived -> NotificationUi.Recommendation
        is Mentioned -> TODO()
    }
}

fun AnnotatedString.Builder.formatAnnotatedString(text: String, vararg boldParts: String?) {
    val parts = text.split(*boldParts.filterNotNull().toTypedArray())
    var index = 0

    for (part in parts) {
        append(part)
        if (index < boldParts.size && boldParts[index] != null) {
            appendBold(boldParts[index]!!)
            index++
        }
    }
}

fun AnnotatedString.Builder.appendBold(text: String) {
    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
    append(text)
    pop()
}

@Composable
fun Long.formatTimeAgoString(): String {
    val now = System.now().toEpochMilliseconds()
    val difference = (now - this).milliseconds

    val minutes = difference.inWholeMinutes
    val hours = difference.inWholeHours
    val days = difference.inWholeDays
    val weeks = days / 7
    val months = days / 30

    return when {
        minutes < 1 -> stringResource(Res.string.time_just_now)
        minutes < 60 -> pluralStringResource(Res.plurals.time_minutes_ago, minutes.toInt(), minutes)
        hours < 24 -> pluralStringResource(Res.plurals.time_hours_ago, hours.toInt(), hours)
        days < 7 -> pluralStringResource(Res.plurals.time_days_ago, days.toInt(), days)
        weeks < 4 -> pluralStringResource(Res.plurals.time_weeks_ago, weeks.toInt(), weeks)
        months < 12 -> pluralStringResource(Res.plurals.time_months_ago, months.toInt(), months)
        else -> pluralStringResource(Res.plurals.time_years_ago, (months / 12).toInt(), months / 12)
    }
}
