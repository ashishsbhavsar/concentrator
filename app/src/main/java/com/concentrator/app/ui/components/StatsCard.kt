package com.concentrator.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.concentrator.app.ui.theme.AccentEmerald
import com.concentrator.app.ui.theme.AccentRose
import com.concentrator.app.ui.theme.SurfaceBorderDark
import com.concentrator.app.ui.theme.SurfaceElevatedDark
import com.concentrator.app.ui.theme.TextPrimary
import com.concentrator.app.ui.theme.TextSecondary
import com.concentrator.app.ui.theme.TextTertiary

@Composable
fun StatsCard(
    title: String,
    count: Int,
    limit: Int,
    accentBrush: Brush,
    iconEmoji: String,
    estimatedMinutes: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (limit > 0) (count.toFloat() / limit).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(600),
        label = "progress_anim"
    )

    val isExceeded = count >= limit
    val isNearLimit = count >= limit * 0.8 && !isExceeded

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceElevatedDark)
            .border(1.dp, SurfaceBorderDark, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(accentBrush),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = iconEmoji, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "~$estimatedMinutes mins spent",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                // Status Badge
                val (badgeText, badgeColor, badgeBg) = when {
                    isExceeded -> Triple("Exceeded", AccentRose, Color(0x26F43F5E))
                    isNearLimit -> Triple("Near limit", Color(0xFFF59E0B), Color(0x26F59E0B))
                    else -> Triple("In budget", AccentEmerald, Color(0x2610B981))
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(badgeBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = badgeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Large Count Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "scrolled",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextTertiary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Text(
                    text = "Goal: $limit",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (isExceeded) AccentRose else Color(0xFF6366F1),
                trackColor = Color(0xFF334155),
                strokeCap = StrokeCap.Round
            )
        }
    }
}
