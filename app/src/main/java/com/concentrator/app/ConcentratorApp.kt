package com.concentrator.app

import android.app.Application
import com.concentrator.app.detector.ScrollTrackerEngine
import com.concentrator.app.service.ScrollNotificationHelper

class ConcentratorApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize notification channels
        ScrollNotificationHelper(this)
        // Warm up tracker engine
        ScrollTrackerEngine.getInstance(this)
    }
}
