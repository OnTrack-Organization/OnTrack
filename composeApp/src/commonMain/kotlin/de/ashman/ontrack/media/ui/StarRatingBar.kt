package de.ashman.ontrack.media.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StarRating(
    modifier: Modifier = Modifier,
    rating: Float = 0f,
    onRatingChanged: (Float) -> Unit
) {
    var selectedRating by remember { mutableStateOf(rating) }
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    val starWidth = 40.dp.toPx()
                    val draggedStars = (dragAmount / starWidth).coerceIn(-0.5f, 0.5f)
                    selectedRating = (selectedRating + draggedStars).coerceIn(0f, 5f)
                    onRatingChanged(selectedRating)
                }
            },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        repeat(5) { index ->
            val starRating = (index + 1).toFloat()
            val animatedScale = remember { Animatable(1f) }
            val isHalfStar = (selectedRating - index) in 0.5f..1f

            LaunchedEffect(selectedRating) {
                val scale = if (selectedRating >= starRating) 1.2f else 1f
                animatedScale.animateTo(scale)
            }

            CompositionLocalProvider(
                LocalRippleConfiguration provides null
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    onClick = {
                        coroutineScope.launch {
                            animatedScale.animateTo(1.2f)
                            animatedScale.animateTo(1f)
                        }
                        selectedRating = starRating
                        onRatingChanged(selectedRating)
                    }
                ) {
                    if (selectedRating >= starRating) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = Color(0xFFFFC700)
                        )
                    } else if (isHalfStar) {
                        Icon(
                            Icons.AutoMirrored.Filled.StarHalf,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = Color(0xFFFFC700)
                        )
                    } else {
                        Icon(
                            Icons.Filled.StarBorder,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = Color(0xFFFFC700)
                        )
                    }
                }
            }
        }
    }
}