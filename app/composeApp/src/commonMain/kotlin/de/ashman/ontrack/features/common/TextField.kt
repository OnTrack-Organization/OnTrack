package de.ashman.ontrack.features.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.search_placeholder
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    placeholder: String,
    closeKeyboard: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    modifier: Modifier = Modifier,
) {
    val isKeyboardOpen by keyboardAsState()

    SearchBar(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        inputField = {
            SearchBarDefaults.InputField(
                modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outline, shape = MaterialTheme.shapes.medium),
                query = query,
                onQueryChange = { newQuery ->
                    onQueryChanged(newQuery)
                },
                onSearch = { closeKeyboard() },
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

@Composable
fun OnTrackCommentTextField(
    modifier: Modifier = Modifier,
    placeholder: String,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
) {
    TextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
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
fun OnTrackTextField(
    modifier: Modifier = Modifier,
    placeholder: String,
    value: String?,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = false,
) {
    TextField(
        modifier = modifier.fillMaxWidth(),
        value = value.orEmpty(),
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        shape = MaterialTheme.shapes.medium,
        singleLine = singleLine,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )
}

@Composable
fun OnTrackUsernameTextField(
    modifier: Modifier = Modifier,
    placeholder: String,
    support: StringResource,
    errorSupport: StringResource? = null,
    prefix: StringResource? = null,
    value: String?,
    enabled: Boolean = true,
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
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
        )
    )
}