package com.concentrator.app.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "scroll_events",
    indices = [
        Index(value = ["dateKey"]),
        Index(value = ["platform", "dateKey"]),
        Index(value = ["timestamp"])
    ]
)
data class ScrollEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val platform: String,
    val timestamp: Long,
    val dateKey: String,
    val hourOfDay: Int,
    val contentSignature: String?,
    val creatorName: String?
)
