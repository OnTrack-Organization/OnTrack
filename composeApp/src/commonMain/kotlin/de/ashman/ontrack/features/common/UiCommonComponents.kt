package de.ashman.ontrack.features.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.search_placeholder
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
        modifier = modifier.fillMaxWidth().height(48.dp),
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
    color: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
) {
    val animatedContentColor by animateColorAsState(targetValue = color)

    OutlinedButton(
        modifier = modifier.fillMaxWidth().height(48.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
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
    modifier: Modifier = Modifier,
    icon: ImageVector,
    enabled: Boolean = true,
    color: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
) {
    val animatedContentColor by animateColorAsState(targetValue = contentColorFor(color))
    val animatedContainerColor by animateColorAsState(targetValue = color)

    Button(
        modifier = modifier.size(48.dp),
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
            modifier = Modifier.size(24.dp),
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
        )
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = icon,
            contentDescription = icon.name,
        )
    }
}

@Composable
fun OnTrackTextField(
    modifier: Modifier = Modifier,
    placeholder: String,
    value: String?,
    onValueChange: (String) -> Unit,
) {
    TextField(
        modifier = modifier.fillMaxWidth(),
        value = value.orEmpty(),
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )
}

@Composable
fun OnTrackUserTextField(
    modifier: Modifier = Modifier,
    placeholder: String,
    support: StringResource,
    errorSupport: StringResource? = null,
    prefix: StringResource? = null,
    value: String?,
    onValueChange: (String) -> Unit,
) {
    TextField(
        modifier = modifier.fillMaxWidth(),
        value = value.orEmpty(),
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        supportingText = {
            if (errorSupport != null) {
                Text(text = stringResource(errorSupport))
            } else {
                Text(text = stringResource(support))
            }
        },
        prefix = { prefix?.let { Text(text = stringResource(it)) } },
        singleLine = true,
        isError = errorSupport != null,
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
        )
    )
}

// TODO maybe create my own
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnTrackCommentTextField(
    modifier: Modifier = Modifier,
    placeholder: String,
    value: String?,
    onValueChange: (String) -> Unit,
) {
    /* BasicTextField(
         modifier = modifier.fillMaxWidth(),
         value = value.orEmpty(),
         onValueChange = onValueChange,
         decorationBox = {
             TextFieldDefaults.DecorationBox(
                 value = value.orEmpty(),
                 visualTransformation = { value },
                 innerTextField = it,
                 placeholder = { Text(placeholder) },
                 singleLine = true,
                 enabled = true,
                 interactionSource = MutableInteractionSource(),
                 contentPadding = PaddingValues(0.dp),
             )
         }
     )*/
}


@Composable
fun PersonImage(
    userImageUrl: String?,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier.size(42.dp),
) {
    val painter = rememberAsyncImagePainter(userImageUrl)
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = modifier
            .clickable(
                enabled = onClick != null,
                onClick = { onClick?.invoke() },
                interactionSource = interactionSource,
                indication = null,
                //indication = ripple(bounded = false, radius = 24.dp),
            ),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        val state = painter.state.collectAsState().value

        when (state) {
            is AsyncImagePainter.State.Empty -> {}
            is AsyncImagePainter.State.Loading -> {
                CircularProgressIndicator()
            }

            is AsyncImagePainter.State.Success -> {
                Image(
                    painter = painter,
                    contentScale = ContentScale.Crop,
                    contentDescription = "Account Image",
                )
            }

            is AsyncImagePainter.State.Error -> {
                Icon(
                    modifier = Modifier.padding(8.dp),
                    imageVector = Icons.Default.Person,
                    contentDescription = "No Image",
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    placeholder: String,
    closeKeyboard: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    modifier: Modifier = Modifier,
) {
    val isKeyboardOpen by keyboardAsState()

    SearchBar(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = { newQuery ->
                    onQueryChanged(newQuery)
                },
                onSearch = {
                    closeKeyboard()
                },
                expanded = false,
                onExpandedChange = { },
                placeholder = { Text(stringResource(Res.string.search_placeholder, placeholder)) },
                leadingIcon = {
                    AnimatedContent(
                        targetState = isKeyboardOpen,
                    ) { targetState ->
                        if (targetState) {
                            IconButton(onClick = closeKeyboard) {
                                Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back Arrow")
                            }
                        } else {
                            Icon(Icons.Default.Search, contentDescription = "Search Icon")
                        }
                    }
                },
                trailingIcon = {
                    AnimatedContent(
                        targetState = query.isNotEmpty(),
                    ) { targetState ->
                        if (targetState) {
                            IconButton(onClick = { onQueryChanged("") }) {
                                Icon(Icons.Rounded.Close, contentDescription = "Clear Search Icon")
                            }
                        }
                    }
                }
            )
        },
        expanded = false,
        onExpandedChange = { },
        colors = SearchBarDefaults.colors(
            containerColor = containerColor,
        ),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 0.dp,
        windowInsets = WindowInsets(top = 0.dp),
    ) {}
}