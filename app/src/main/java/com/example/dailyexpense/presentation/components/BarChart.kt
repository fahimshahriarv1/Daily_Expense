package com.example.dailyexpense.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailyexpense.domain.model.ExpenseStats

@Composable
fun BarChart(
    data: List<ExpenseStats>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
    labelColor: Color = MaterialTheme.colorScheme.onSurface
) {
    if (data.isEmpty()) return

    val maxAmount = data.maxOf { it.totalAmount }.coerceAtLeast(1.0)

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 8.dp)
        ) {
            val barCount = data.size
            val totalSpacing = size.width * 0.2f
            val barSpacing = totalSpacing / (barCount + 1)
            val barWidth = (size.width - totalSpacing) / barCount
            val maxBarHeight = size.height - 24.dp.toPx()

            data.forEachIndexed { index, stat ->
                val barHeight = if (maxAmount > 0) {
                    (stat.totalAmount / maxAmount * maxBarHeight).toFloat()
                } else 0f

                val x = barSpacing + index * (barWidth + barSpacing)
                val y = size.height - barHeight

                // Draw bar with rounded top corners
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )

                // Draw amount on top of bar
                if (stat.totalAmount > 0) {
                    val paint = android.graphics.Paint().apply {
                        color = labelColor.hashCode()
                        textSize = 10.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    drawContext.canvas.nativeCanvas.drawText(
                        formatAmount(stat.totalAmount),
                        x + barWidth / 2,
                        y - 4.dp.toPx(),
                        paint
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Labels row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.forEach { stat ->
                Text(
                    text = stat.label,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
            }
        }
    }
}

private fun formatAmount(amount: Double): String {
    return if (amount >= 1000) {
        String.format("%.0fk", amount / 1000)
    } else {
        String.format("%.0f", amount)
    }
}
