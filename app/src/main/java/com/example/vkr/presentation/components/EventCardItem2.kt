package com.example.vkr.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vkr.data.local.model.EventEntity

@Composable
fun EventCardItem2(event: EventEntity, painter: Painter, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    val isDone = event.verified || event.completed
    val textColor = if (isDone) colorScheme.onSurfaceVariant else colorScheme.onSurface
    val parsedDateTime = DateTimeUtils.parseIsoFormatted(event.dateTime)
    val displayDateTime = parsedDateTime?.let { DateTimeUtils.formatDisplay(it) } ?: "Ошибка загрузки времени"

    val statusText = when {
        event.verified -> "Завершено"
        event.completed -> "На проверке"
        else -> null
    }
    val statusColor = when {
        event.verified -> colorScheme.error
        event.completed -> colorScheme.tertiary
        else -> Color.Unspecified
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = event.title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
        Text(
            text = event.locationName,
            style = MaterialTheme.typography.bodySmall,
            color = textColor
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = displayDateTime,
            style = MaterialTheme.typography.labelSmall,
            color = colorScheme.onSurfaceVariant
        )
        statusText?.let {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = it,
                color = statusColor,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}