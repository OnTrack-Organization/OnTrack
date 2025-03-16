package de.ashman.ontrack.features.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedButtonContent(
    text: StringResource,
    icon: ImageVector?,
) {
    AnimatedContent(
        targetState = Pair(text, icon),
        transitionSpec = {
            fadeIn() togetherWith fadeOut() using SizeTransform(false)
        }
    ) { (targetText, targetIcon) ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            targetIcon?.let {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = it,
                    contentDescription = it.name,
                )
            }
            Text(
                text = stringResource(targetText),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
fun OnTrackButton(
    modifier: Modifier = Modifier,
    text: StringResource,
    icon: ImageVector? = null,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    color: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
) {
    val animatedContainerColor by animateColorAsState(targetValue = color)
    val animatedContentColor by animateColorAsState(targetValue = contentColorFor(color))

    Button(
        modifier = modifier.height(48.dp),
        onClick = onClick,
        enabled = !isLoading && enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = animatedContainerColor,
            contentColor = animatedContentColor,
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = animatedContentColor,
                strokeWidth = 3.dp,
            )
        } else {
            AnimatedButtonContent(
                text = text,
                icon = icon,
            )
        }
    }
}

@Composable
fun OnTrackOutlinedButton(
    modifier: Modifier = Modifier,
    text: StringResource,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    color: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
) {
    val animatedContentColor by animateColorAsState(targetValue = color)

    OutlinedButton(
        modifier = modifier.height(48.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = animatedContentColor,
        )
    ) {
        AnimatedButtonContent(
            text = text,
            icon = icon,
        )
    }
}

@Composable
fun OnTrackIconButton(
    modifier: Modifier = Modifier.size(48.dp),
    icon: ImageVector,
    enabled: Boolean = true,
    color: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
) {
    val animatedContentColor by animateColorAsState(targetValue = contentColorFor(color))
    val animatedContainerColor by animateColorAsState(targetValue = color)

    Button(
        modifier = modifier,
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(0.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            contentColor = animatedContentColor,
            containerColor = animatedContainerColor,
        )

    ) {
        Icon(
            modifier = Modifier.fillMaxSize(0.5f),
            imageVector = icon,
            contentDescription = icon.name,
        )
    }
}

@Composable
fun OnTrackOutlinedIconButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    enabled: Boolean = true,
    color: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
) {
    val animatedContentColor by animateColorAsState(targetValue = contentColorFor(color))

    OutlinedButton(
        modifier = modifier.size(48.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(0.dp),
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = animatedContentColor,
        ),
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = icon,
            contentDescription = icon.name,
        )
    }
}