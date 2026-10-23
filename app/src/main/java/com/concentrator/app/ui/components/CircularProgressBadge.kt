package com.concentrator.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.concentrator.app.ui.theme.AccentEmerald
import com.concentrator.app.ui.theme.AccentRose
import com.concentrator.app.ui.theme.PrimaryIndigo
import com.concentrator.app.ui.theme.TextPrimary
import com.concentrator.app.ui.theme.TextSecondary
import com.concentrator.app.ui.theme.TextTertiary

@Composable
fun CircularProgressBadge(
    current: Int,
    maxLimit: Int,
    size: Dp = 190.dp,
    strokeWidth: Dp = 14.dp,
    modifier: Modifier = Modifier
) {
    val progress = if (maxLimit > 0) (current.toFloat() / maxLimit).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(700),
        label = "circular_sweep_anim"
    )

    val isExceeded = current >= maxLimit
    val arcColor = if (isExceeded) AccentRose else if (current >= maxLimit * 0.8) Color(0xFFF59E0B) else PrimaryIndigo

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            // Track Arc
            drawArc(
                color = Color(0xFF1E293B),
                startAngle = 140f,
                sweepAngle = 260f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Progress Arc
            drawArc(
                color = arcColor,
                startAngle = 140f,
                sweepAngle = 260f * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = current.toString(),
                style = MaterialTheme.typography.displayLarge,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
            Text(
                text = "Total Videos",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (isExceeded) "Limit Exceeded!" else "${(maxLimit - current).coerceAtLeast(0)} left today",
                style = MaterialTheme.typography.labelMedium,
                color = if (isExceeded) AccentRose else AccentEmerald,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
