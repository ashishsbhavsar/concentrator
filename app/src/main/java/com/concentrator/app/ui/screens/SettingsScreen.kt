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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.concentrator.app.ui.theme.AccentRose
import com.concentrator.app.ui.theme.BackgroundDark
import com.concentrator.app.ui.theme.PrimaryIndigo
import com.concentrator.app.ui.theme.SurfaceBorderDark
import com.concentrator.app.ui.theme.SurfaceElevatedDark
import com.concentrator.app.ui.theme.TextPrimary
import com.concentrator.app.ui.theme.TextSecondary
import com.concentrator.app.ui.viewmodel.SettingsViewModel
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val preferences by viewModel.preferences.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Text(
            text = "Settings & Limits",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Tune your daily scroll budgets and alerts",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Daily Reel Limit
        Box(
            modifier = Modifier
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
                        text = "📸 Instagram Reels Limit",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${preferences.reelsLimit} reels",
                        style = MaterialTheme.typography.titleMedium,
                        color = PrimaryIndigo,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = preferences.reelsLimit.toFloat(),
                    onValueChange = { viewModel.updateReelsLimit(it.roundToInt()) },
                    valueRange = 10f..200f,
                    steps = 18,
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryIndigo,
                        activeTrackColor = PrimaryIndigo,
                        inactiveTrackColor = Color(0xFF334155)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Daily Short Limit
        Box(
            modifier = Modifier
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
                        text = "🎬 YouTube Shorts Limit",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${preferences.shortsLimit} shorts",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFFF4D4D),
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = preferences.shortsLimit.toFloat(),
                    onValueChange = { viewModel.updateShortsLimit(it.roundToInt()) },
                    valueRange = 10f..200f,
                    steps = 18,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFFF4D4D),
                        activeTrackColor = Color(0xFFFF4D4D),
                        inactiveTrackColor = Color(0xFF334155)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Alerts & Notifications",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Notifications switch
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceElevatedDark)
                .border(1.dp, SurfaceBorderDark, RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Limit Notification Alerts",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Nudge when you exceed your daily scroll count",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                Switch(
                    checked = preferences.isNotificationsEnabled,
                    onCheckedChange = { viewModel.updateNotificationsEnabled(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PrimaryIndigo
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Vibration switch
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceElevatedDark)
                .border(1.dp, SurfaceBorderDark, RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Haptic Vibration",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Tactile alert pulse upon hitting the limit",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                Switch(
                    checked = preferences.isVibrationEnabled,
                    onCheckedChange = { viewModel.updateVibrationEnabled(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PrimaryIndigo
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Danger Zone
        Text(
            text = "Data Management",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { showResetDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentRose)
        ) {
            Text(text = "Reset All Scroll Data", fontWeight = FontWeight.SemiBold)
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text(text = "Reset Scroll History?") },
                text = { Text(text = "This will permanently delete all logged reel and short scrolls from your device.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.clearAllHistory()
                            showResetDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentRose)
                    ) {
                        Text(text = "Delete")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showResetDialog = false }) {
                        Text(text = "Cancel")
                    }
                }
            )
        }
    }
}
