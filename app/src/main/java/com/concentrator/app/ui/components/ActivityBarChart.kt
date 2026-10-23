package com.concentrator.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.concentrator.app.data.model.HourlyCount
import com.concentrator.app.ui.theme.PrimaryIndigo
import com.concentrator.app.ui.theme.SurfaceBorderDark
import com.concentrator.app.ui.theme.SurfaceElevatedDark
import com.concentrator.app.ui.theme.TextSecondary
import com.concentrator.app.ui.theme.TextTertiary
import java.time.LocalTime

@Composable
fun ActivityBarChart(
    hourlyData: List<HourlyCount>,
    modifier: Modifier = Modifier
) {
    val currentHour = LocalTime.now().hour
    val maxCount = (hourlyData.maxOfOrNull { it.count } ?: 1).coerceAtLeast(1)

    // Build 24 hour slot map
    val countMap = hourlyData.associate { it.hourOfDay to it.count }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceElevatedDark)
            .border(1.dp, SurfaceBorderDark, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Scroll Heatmap",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Peak: $maxCount / hr",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextTertiary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 24 Hour Bars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                for (hour in 0..23) {
                    val count = countMap[hour] ?: 0
                    val barRatio = (count.toFloat() / maxCount).coerceIn(0.06f, 1f)
                    val isCurrent = hour == currentHour

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(barRatio)
                                .width(if (isCurrent) 6.dp else 4.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    when {
                                        count == 0 -> Color(0xFF334155)
                                        isCurrent -> Color(0xFF38BDF8)
                                        count > maxCount * 0.7 -> Color(0xFFEC4899)
                                        else -> PrimaryIndigo
                                    }
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Time Axis Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "12 AM", style = MaterialTheme.typography.labelMedium, fontSize = 10.sp)
                Text(text = "6 AM", style = MaterialTheme.typography.labelMedium, fontSize = 10.sp)
                Text(text = "12 PM", style = MaterialTheme.typography.labelMedium, fontSize = 10.sp)
                Text(text = "6 PM", style = MaterialTheme.typography.labelMedium, fontSize = 10.sp)
                Text(text = "Now", style = MaterialTheme.typography.labelMedium, fontSize = 10.sp, color = Color(0xFF38BDF8))
            }
        }
    }
}
