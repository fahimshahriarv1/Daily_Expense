package com.fahimshahriarv1.dailyexpense.presentation.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.fahimshahriarv1.dailyexpense.domain.model.ExpenseStats

@Composable
fun BarChart(
    data: List<ExpenseStats>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    incomeData: List<ExpenseStats> = emptyList(),
    incomeBarColor: Color = MaterialTheme.colorScheme.tertiary
) {
    if (data.isEmpty()) return

    val hasIncome = incomeData.isNotEmpty()
    val maxAmount = if (hasIncome) {
        maxOf(
            data.maxOf { it.totalAmount },
            incomeData.maxOfOrNull { it.totalAmount } ?: 0.0
        ).coerceAtLeast(1.0)
    } else {
        data.maxOf { it.totalAmount }.coerceAtLeast(1.0)
    }

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 8.dp)
        ) {
            val barCount = data.size
            val totalSpacing = size.width * 0.2f
            val groupSpacing = totalSpacing / (barCount + 1)
            val groupWidth = (size.width - totalSpacing) / barCount
            val maxBarHeight = size.height - 24.dp.toPx()

            if (hasIncome) {
                val barWidth = groupWidth * 0.45f
                val gap = groupWidth * 0.1f

                data.forEachIndexed { index, stat ->
                    val x = groupSpacing + index * (groupWidth + groupSpacing)

                    // Income bar (left)
                    val incomeAmount = incomeData.getOrNull(index)?.totalAmount ?: 0.0
                    val incomeHeight = if (maxAmount > 0) {
                        (incomeAmount / maxAmount * maxBarHeight).toFloat()
                    } else 0f

                    if (incomeHeight > 0) {
                        drawRoundRect(
                            color = incomeBarColor,
                            topLeft = Offset(x, size.height - incomeHeight),
                            size = Size(barWidth, incomeHeight),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )
                    }

                    // Expense bar (right)
                    val expenseHeight = if (maxAmount > 0) {
                        (stat.totalAmount / maxAmount * maxBarHeight).toFloat()
                    } else 0f

                    if (expenseHeight > 0) {
                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(x + barWidth + gap, size.height - expenseHeight),
                            size = Size(barWidth, expenseHeight),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )
                    }
                }
            } else {
                val barWidth = groupWidth

                data.forEachIndexed { index, stat ->
                    val barHeight = if (maxAmount > 0) {
                        (stat.totalAmount / maxAmount * maxBarHeight).toFloat()
                    } else 0f

                    val x = groupSpacing + index * (barWidth + groupSpacing)
                    val y = size.height - barHeight

                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )

                    if (stat.totalAmount > 0) {
                        val paint = Paint().apply {
                            color = labelColor.hashCode()
                            textSize = 10.sp.toPx()
                            textAlign = Paint.Align.CENTER
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

        // Legend
        if (hasIncome) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = incomeBarColor,
                    modifier = Modifier.size(10.dp)
                ) {}
                Spacer(modifier = Modifier.width(4.dp))
                Text("Income", style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.width(16.dp))
                Surface(
                    shape = CircleShape,
                    color = barColor,
                    modifier = Modifier.size(10.dp)
                ) {}
                Spacer(modifier = Modifier.width(4.dp))
                Text("Expense", style = MaterialTheme.typography.labelSmall)
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
