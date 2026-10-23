package com.concentrator.app.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.concentrator.app.ui.components.ActivityBarChart
import com.concentrator.app.ui.components.CircularProgressBadge
import com.concentrator.app.ui.components.PermissionStatusCard
import com.concentrator.app.ui.components.StatsCard
import com.concentrator.app.ui.theme.AccentEmerald
import com.concentrator.app.ui.theme.AccentRose
import com.concentrator.app.ui.theme.BackgroundDark
import com.concentrator.app.ui.theme.InstagramBrush
import com.concentrator.app.ui.theme.PrimaryIndigo
import com.concentrator.app.ui.theme.SurfaceBorderDark
import com.concentrator.app.ui.theme.SurfaceDark
import com.concentrator.app.ui.theme.SurfaceElevatedDark
import com.concentrator.app.ui.theme.TextPrimary
import com.concentrator.app.ui.theme.TextSecondary
import com.concentrator.app.ui.theme.TextTertiary
import com.concentrator.app.ui.theme.YouTubeBrush
import com.concentrator.app.ui.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val stats by viewModel.statSummary.collectAsState()
    val isAccessibilityGranted by viewModel.isAccessibilityGranted.collectAsState()
    val isOverlayGranted by viewModel.isOverlayGranted.collectAsState()
    val isOverlayRunning by viewModel.isOverlayRunning.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        // App Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Concentrator",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Reel & Short Scroll Tracker",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            // Accessibility Live Status Chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isAccessibilityGranted) Color(0x2610B981) else Color(0x26F43F5E))
                    .border(
                        1.dp,
                        if (isAccessibilityGranted) Color(0x4D10B981) else Color(0x4DF43F5E),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isAccessibilityGranted) AccentEmerald else AccentRose)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAccessibilityGranted) "Tracker Active" else "Setup Needed",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isAccessibilityGranted) AccentEmerald else AccentRose
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Warning Banner if Accessibility is not granted
        if (!isAccessibilityGranted) {
            PermissionStatusCard(
                title = "Enable Scroll Tracking",
                description = "Concentrator requires Accessibility permission to detect Reels and Shorts scrolling inside Instagram and YouTube.",
                isGranted = false,
                actionButtonText = "Enable Now",
                onActionClick = {
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Circular Gauge Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(SurfaceDark)
                .border(1.dp, SurfaceBorderDark, RoundedCornerShape(24.dp))
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressBadge(
                current = stats.totalCount,
                maxLimit = stats.totalLimit
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Platform Metric Cards (Instagram & YouTube)
        StatsCard(
            title = "Instagram Reels",
            count = stats.reelsCount,
            limit = stats.reelsLimit,
            accentBrush = InstagramBrush,
            iconEmoji = "📸",
            estimatedMinutes = (stats.reelsCount * 25) / 60
        )

        Spacer(modifier = Modifier.height(12.dp))

        StatsCard(
            title = "YouTube Shorts",
            count = stats.shortsCount,
            limit = stats.shortsLimit,
            accentBrush = YouTubeBrush,
            iconEmoji = "🎬",
            estimatedMinutes = (stats.shortsCount * 25) / 60
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Floating Overlay Controller Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceElevatedDark)
                .border(1.dp, SurfaceBorderDark, RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Floating Counter HUD",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Displays live scroll count overlay on top of Instagram & YouTube",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                if (!isOverlayGranted) {
                    Button(
                        onClick = {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}")
                            ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Grant", fontSize = 12.sp)
                    }
                } else {
                    Switch(
                        checked = isOverlayRunning,
                        onCheckedChange = { viewModel.toggleFloatingOverlay() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryIndigo
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 24-Hour Scroll Activity Heatmap
        ActivityBarChart(hourlyData = stats.hourlyCounts)

        Spacer(modifier = Modifier.height(20.dp))

        // Developer / Testing simulation buttons
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x1A6366F1))
                .border(1.dp, Color(0x336366F1), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "Quick Test & Simulator",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryIndigo
                )
                Text(
                    text = "Verify counter logic and floating HUD without needing to open social apps:",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.simulateTestReel() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "+1 Reel", fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = { viewModel.simulateTestShort() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "+1 Short", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
