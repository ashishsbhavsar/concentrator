package com.concentrator.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.concentrator.app.data.local.AppDatabase
import com.concentrator.app.data.model.PlatformType
import com.concentrator.app.ui.theme.AccentEmerald
import com.concentrator.app.ui.theme.BackgroundDark
import com.concentrator.app.ui.theme.InstagramBrush
import com.concentrator.app.ui.theme.PrimaryIndigo
import com.concentrator.app.ui.theme.SurfaceBorderDark
import com.concentrator.app.ui.theme.SurfaceElevatedDark
import com.concentrator.app.ui.theme.TextPrimary
import com.concentrator.app.ui.theme.TextSecondary
import com.concentrator.app.ui.theme.TextTertiary
import com.concentrator.app.ui.theme.YouTubeBrush

@Composable
fun AnalyticsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val historyItems by db.scrollDao().observeRecentHistory().collectAsState(initial = emptyList())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        item {
            Text(
                text = "Scroll Analytics",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Track your long-term consumption habits",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Summary Wellbeing Card
        item {
            val totalLogged = historyItems.sumOf { it.count }
            val estimatedHours = (totalLogged * 25) / 3600f

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceElevatedDark)
                    .border(1.dp, SurfaceBorderDark, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "Total Recorded Screen Time",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format("%.1f", estimatedHours),
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryIndigo
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "hours spent watching reels & shorts",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "💡 Based on an average of 25 seconds per short video.",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextTertiary
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Recent Daily Log",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (historyItems.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No history logged yet. Start scrolling or use the simulator to view history!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextTertiary
                    )
                }
            }
        } else {
            items(historyItems) { item ->
                val isInstagram = item.platform == PlatformType.INSTAGRAM_REELS.name
                val icon = if (isInstagram) "📸" else "🎬"
                val platformName = if (isInstagram) "Instagram Reels" else "YouTube Shorts"
                val brush = if (isInstagram) InstagramBrush else YouTubeBrush

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceElevatedDark)
                        .border(1.dp, SurfaceBorderDark, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(brush),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = icon, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = platformName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = item.dateKey,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextTertiary
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${item.count}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "~${(item.count * 25) / 60}m",
                                style = MaterialTheme.typography.labelMedium,
                                color = AccentEmerald
                            )
                        }
                    }
                }
            }
        }
    }
}
