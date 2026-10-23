package com.concentrator.app.data.model

data class ScrollEvent(
    val id: Long = 0,
    val platform: PlatformType,
    val timestamp: Long = System.currentTimeMillis(),
    val contentSignature: String? = null,
    val creatorName: String? = null
)
