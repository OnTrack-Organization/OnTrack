package de.ashman.ontrack.features.detail.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pushpal.jetlime.EventPointType
import com.pushpal.jetlime.ItemsList
import com.pushpal.jetlime.JetLimeColumn
import com.pushpal.jetlime.JetLimeDefaults
import com.pushpal.jetlime.JetLimeEvent
import com.pushpal.jetlime.JetLimeEventDefaults
import de.ashman.ontrack.domain.media.MediaType
import de.ashman.ontrack.domain.tracking.Entry
import de.ashman.ontrack.features.common.MiniStarRatingBar
import de.ashman.ontrack.features.common.getColor
import de.ashman.ontrack.features.common.getIcon
import de.ashman.ontrack.features.common.getLabel
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.detail_timeline_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun TimelineSheet(
    mediaType: MediaType,
    entries: List<Entry>,
) {
    Text(
        text = stringResource(Res.string.detail_timeline_title),
        style = MaterialTheme.typography.titleMedium,
    )

    JetLimeColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 500.dp),
        itemsList = ItemsList(entries),
        style = JetLimeDefaults.columnStyle(
            itemSpacing = 32.dp,
        )
    ) { index, item, position ->
        JetLimeEvent(
            style = JetLimeEventDefaults.eventStyle(
                position = position,
                pointStrokeWidth = 0.dp,
                pointRadius = 16.dp,
                pointColor = item.status.getColor(),
                pointFillColor = item.status.getColor(),
                pointStrokeColor = item.status.getColor(),
                pointType = EventPointType.custom(
                    icon = paddedPainter(rememberVectorPainter(item.status.getIcon(true))),
                    tint = contentColorFor(item.status.getColor()),
                )
            ),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(item.status.getLabel(mediaType)),
                    fontWeight = FontWeight.Bold,
                )

                item.rating?.let {
                    MiniStarRatingBar(
                        rating = item.rating,
                        starColor = contentColorFor(item.status.getColor()),
                    )
                }

                Column {
                    item.reviewTitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    item.reviewDescription?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }
                }
            }
        }
    }
}

fun paddedPainter(
    original: Painter,
    padding: Dp = 8.dp
): Painter {
    return object : Painter() {
        override val intrinsicSize = original.intrinsicSize

        override fun DrawScope.onDraw() {
            val padPx = padding.toPx()
            val paddedSize = size.copy(
                width = size.width - padPx * 2,
                height = size.height - padPx * 2
            )

            withTransform({
                translate(left = padPx, top = padPx)
            }) {
                with(original) {
                    draw(size = paddedSize)
                }
            }
        }
    }
}
