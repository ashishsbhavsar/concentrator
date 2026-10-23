package com.concentrator.app.data.model

enum class PlatformType(val displayName: String, val packageName: String) {
    INSTAGRAM_REELS("Instagram Reels", "com.instagram.android"),
    YOUTUBE_SHORTS("YouTube Shorts", "com.google.android.youtube");

    companion object {
        fun fromPackageName(packageName: String): PlatformType? {
            return entries.firstOrNull { it.packageName == packageName }
        }
    }
}
