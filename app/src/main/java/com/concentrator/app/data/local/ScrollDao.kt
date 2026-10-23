package com.concentrator.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class PlatformCount(
    val platform: String,
    val count: Int
)

data class HourlyStat(
    val hourOfDay: Int,
    val count: Int
)

data class DailyHistoryItem(
    val dateKey: String,
    val platform: String,
    val count: Int
)

@Dao
interface ScrollDao {

    @Insert
    suspend fun insert(event: ScrollEntity): Long

    @Query("SELECT COUNT(*) FROM scroll_events WHERE dateKey = :dateKey")
    fun observeTotalCountForDate(dateKey: String): Flow<Int>

    @Query("SELECT platform, COUNT(*) as count FROM scroll_events WHERE dateKey = :dateKey GROUP BY platform")
    fun observePlatformCountsForDate(dateKey: String): Flow<List<PlatformCount>>

    @Query("SELECT hourOfDay, COUNT(*) as count FROM scroll_events WHERE dateKey = :dateKey GROUP BY hourOfDay ORDER BY hourOfDay ASC")
    fun observeHourlyStatsForDate(dateKey: String): Flow<List<HourlyStat>>

    @Query("SELECT COUNT(*) FROM scroll_events WHERE dateKey = :dateKey AND platform = :platform")
    suspend fun getCountForPlatformToday(dateKey: String, platform: String): Int

    @Query("SELECT COUNT(*) FROM scroll_events WHERE dateKey = :dateKey")
    suspend fun getTotalCountToday(dateKey: String): Int

    @Query("SELECT dateKey, platform, COUNT(*) as count FROM scroll_events GROUP BY dateKey, platform ORDER BY dateKey DESC LIMIT 14")
    fun observeRecentHistory(): Flow<List<DailyHistoryItem>>

    @Query("DELETE FROM scroll_events WHERE dateKey = :dateKey")
    suspend fun clearDate(dateKey: String)

    @Query("DELETE FROM scroll_events")
    suspend fun clearAll()
}
