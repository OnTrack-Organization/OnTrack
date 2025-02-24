package de.ashman.ontrack.features.detail.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.ashman.ontrack.features.common.PersonImage
import de.ashman.ontrack.features.common.contentSizeAnimation
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MediaDescription(
    title: String?,
    description: String?,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    var hasOverflow by remember { mutableStateOf(false) }

    description?.let {
        Column(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    expanded = !expanded
                }
                .contentSizeAnimation(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                title?.let {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                if (hasOverflow) {
                    AnimatedContent(
                        targetState = expanded,
                    ) { targetState ->
                        Icon(
                            imageVector = if (targetState) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                        )
                    }
                }
            }

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (expanded) Int.MAX_VALUE else 4,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = {
                    if (!expanded) hasOverflow = it.hasVisualOverflow
                }
            )
        }
    }
}

@Composable
fun MediaChips(
    title: String,
    items: List<String>,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(items) {
                OutlinedCard(
                    modifier = Modifier.width(IntrinsicSize.Max),
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                        text = it,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

@Composable
fun CreatorCard(
    title: StringResource,
    name: String?,
    subInfo: String?,
    description: String?,
    imageUrl: String?,
) {
    var expanded by remember { mutableStateOf(false) }

    name?.let {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    expanded = !expanded
                }
                .contentSizeAnimation(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.titleMedium,
                )
                description?.let {
                    AnimatedContent(
                        targetState = expanded,
                    ) { targetState ->
                        Icon(
                            imageVector = if (targetState) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PersonImage(
                    userImageUrl = imageUrl,
                )

                Column(
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    subInfo?.let {
                        Text(
                            text = subInfo,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            description?.let {
                if (expanded) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
