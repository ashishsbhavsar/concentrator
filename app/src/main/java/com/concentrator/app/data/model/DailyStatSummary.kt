package com.concentrator.app.data.model

data class DailyStatSummary(
    val reelsCount: Int = 0,
    val shortsCount: Int = 0,
    val totalCount: Int = 0,
    val estimatedMinutes: Int = 0,
    val reelsLimit: Int = 50,
    val shortsLimit: Int = 50,
    val totalLimit: Int = 100,
    val hourlyCounts: List<HourlyCount> = emptyList()
) {
    val reelsProgress: Float
        get() = if (reelsLimit > 0) (reelsCount.toFloat() / reelsLimit).coerceIn(0f, 1f) else 0f

    val shortsProgress: Float
        get() = if (shortsLimit > 0) (shortsCount.toFloat() / shortsLimit).coerceIn(0f, 1f) else 0f

    val totalProgress: Float
        get() = if (totalLimit > 0) (totalCount.toFloat() / totalLimit).coerceIn(0f, 1f) else 0f

    val isReelsExceeded: Boolean
        get() = reelsCount >= reelsLimit

    val isShortsExceeded: Boolean
        get() = shortsCount >= shortsLimit

    val isTotalExceeded: Boolean
        get() = totalCount >= totalLimit
}

data class HourlyCount(
    val hourOfDay: Int, // 0..23
    val count: Int
)
