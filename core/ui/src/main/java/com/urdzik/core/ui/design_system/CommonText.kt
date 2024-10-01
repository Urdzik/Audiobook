package com.urdzik.core.ui.design_system

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.urdzik.core.ui.shimmerBrush

@Composable
fun CommonText(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    textAlign: TextAlign = TextAlign.Center,
    ) {
    Text(
        text = text,
        style = style,
        textAlign = textAlign,
        modifier = modifier
            .padding(4.dp)
            .background(
                shimmerBrush(
                    targetValue = 1300f,
                    showShimmer = text.isEmpty()
                )
            )
            .fillMaxWidth()
    )
}