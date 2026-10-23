package com.concentrator.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavItem(val route: String, val title: String, val icon: ImageVector) {
    data object Dashboard : NavItem("dashboard", "Dashboard", Icons.Default.Home)
    data object Analytics : NavItem("analytics", "Analytics", Icons.Default.BarChart)
    data object Settings : NavItem("settings", "Settings", Icons.Default.Settings)

    companion object {
        val items = listOf(Dashboard, Analytics, Settings)
    }
}
