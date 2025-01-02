package de.ashman.ontrack.media.domain

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.shelves_filled
import ontrack.composeapp.generated.resources.shelves_outlined
import ontrack.composeapp.generated.resources.status_all
import ontrack.composeapp.generated.resources.status_binged
import ontrack.composeapp.generated.resources.status_binging
import ontrack.composeapp.generated.resources.status_catalog
import ontrack.composeapp.generated.resources.status_dropped
import ontrack.composeapp.generated.resources.status_listened
import ontrack.composeapp.generated.resources.status_played
import ontrack.composeapp.generated.resources.status_playing
import ontrack.composeapp.generated.resources.status_read
import ontrack.composeapp.generated.resources.status_reading
import ontrack.composeapp.generated.resources.status_watched
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.vectorResource

@Serializable
enum class ConsumeStatus {
    ALL,
    BINGING,
    PLAYING,
    READING,
    WATCHED,
    BINGED,
    READ,
    PLAYED,
    LISTENED,
    DROPPED,
    CATALOG;

    fun getConsumeStatusLabel(): StringResource {
        return when (this) {
            ALL -> Res.string.status_all
            BINGING -> Res.string.status_binging
            PLAYING -> Res.string.status_playing
            READING -> Res.string.status_reading
            WATCHED -> Res.string.status_watched
            BINGED -> Res.string.status_binged
            READ -> Res.string.status_read
            PLAYED -> Res.string.status_played
            LISTENED -> Res.string.status_listened
            DROPPED -> Res.string.status_dropped
            CATALOG -> Res.string.status_catalog
        }
    }

    @Composable
    fun getConsumeStatusIcon(isFilled: Boolean = false): ImageVector {
        return when (this) {
            BINGING, PLAYING, READING ->
                if (isFilled) Icons.Filled.Visibility else Icons.Outlined.Visibility

            WATCHED, BINGED, READ, PLAYED, LISTENED ->
                if (isFilled) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle

            DROPPED ->
                if (isFilled) Icons.Filled.Cancel else Icons.Outlined.Cancel

            CATALOG ->
                if (isFilled) Icons.Filled.BookmarkAdd else Icons.Outlined.BookmarkAdd

            ALL ->
                if (isFilled) vectorResource(Res.drawable.shelves_filled) else vectorResource(Res.drawable.shelves_outlined)
        }
    }
}