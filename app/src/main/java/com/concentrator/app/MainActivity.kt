package com.concentrator.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.concentrator.app.ui.navigation.AppNavigation
import com.concentrator.app.ui.theme.ConcentratorTheme
import com.concentrator.app.ui.viewmodel.DashboardViewModel
import com.concentrator.app.ui.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ConcentratorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        dashboardViewModel = dashboardViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh permission statuses whenever user returns to the app
        dashboardViewModel.refreshPermissionStates()
    }
}
