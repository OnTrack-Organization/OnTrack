package de.ashman.ontrack.features.notifications

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import kotlinx.datetime.Clock.System
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.notifications_comment
import ontrack.composeapp.generated.resources.notifications_like
import ontrack.composeapp.generated.resources.notifications_recommendation
import ontrack.composeapp.generated.resources.notifications_reply
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

sealed class NotificationTypeUi(
    val icon: ImageVector,
    val message: @Composable (NotificationData) -> AnnotatedString
) {
    data object Like : NotificationTypeUi(
        icon = Icons.Default.Favorite,
        message = { data ->
            buildAnnotatedString {
                val text = stringResource(
                    Res.string.notifications_like,
                    data.senderUserName,
                    data.mediaTitle
                )
                formatAnnotatedString(text, data.senderUserName, data.mediaTitle)
            }
        }
    )

    data object Comment : NotificationTypeUi(
        icon = Icons.AutoMirrored.Default.Chat,
        message = { data ->
            buildAnnotatedString {
                val text = stringResource(
                    Res.string.notifications_comment,
                    data.senderUserName,
                    data.mediaTitle
                )
                formatAnnotatedString(text, data.senderUserName, data.mediaTitle)
            }
        }
    )

    data object Reply : NotificationTypeUi(
        icon = Icons.Default.AlternateEmail,
        message = { data ->
            buildAnnotatedString {
                val text = stringResource(
                    Res.string.notifications_reply,
                    data.senderUserName,
                    data.repliedToUserName.orEmpty(),
                    data.mediaTitle
                )
                formatAnnotatedString(text, data.senderUserName, data.repliedToUserName, data.mediaTitle)
            }
        }
    )

    data object Recommendation : NotificationTypeUi(
        icon = Icons.Default.VolunteerActivism,
        message = { data ->
            buildAnnotatedString {
                val text = stringResource(
                    Res.string.notifications_recommendation,
                    data.senderUserName,
                    data.mediaTitle
                )
                formatAnnotatedString(text, data.senderUserName, data.mediaTitle)
            }
        }
    )
}

fun NotificationType.getUI(): NotificationTypeUi {
    return when (this) {
        NotificationType.LIKE -> NotificationTypeUi.Like
        NotificationType.COMMENT -> NotificationTypeUi.Comment
        NotificationType.REPLY -> NotificationTypeUi.Reply
        NotificationType.RECOMMENDATION -> NotificationTypeUi.Recommendation
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
